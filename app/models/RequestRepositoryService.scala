package models

import akka.actor.ClassicActorSystemProvider
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.Configuration

import java.util.concurrent.{TimeUnit, TimeoutException}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Success

@ImplementedBy(classOf[RequestRepositoryServiceImpl])
trait RequestRepositoryService {
  def addRequest(realm: String, login: String): Future[Boolean]
  def cancelRequest(realm: String, login: String): Future[Boolean]
  def getRequest(realm: String, login: String): Option[Promise[Boolean]]

  def confirm(realm: String, login: String): Unit
  def reject(realm: String, login: String): Unit
}

@Singleton
class RequestRepositoryServiceImpl @Inject()(config: Configuration)(implicit ec: ExecutionContext,
                                                                    ap: ClassicActorSystemProvider) extends RequestRepositoryService {

  private val timeOut = config.get[Long]("mobile.timeOut")

  private val requests = scala.collection.mutable.Map[String, Promise[Boolean]]()

  private def key(realm: String, login: String) = s"${realm}:${login}"
  override def addRequest(realm: String, login: String): Future[Boolean] =
    {
        this.synchronized{
          if(requests.contains(key(realm, login))){
            val p = requests.get(key(realm, login))
            requests.remove(key(realm, login))
            p.foreach(pr=>if(!pr.isCompleted) {pr.success(false)})
          }
          val newPromise = Promise[Boolean]()
          requests(key(realm, login)) = newPromise
          Future.firstCompletedOf(Seq(newPromise.future,
              akka.pattern.after(Duration(timeOut, TimeUnit.SECONDS))(Future.failed(new TimeoutException()))))
            .recoverWith{
              case e: TimeoutException =>
                cancelRequest(realm, login)
            }

      }
    }

  override def cancelRequest(realm: String, login: String): Future[Boolean] = this.synchronized{
    requests.get(key(realm, login)).foreach(_.success(false))
    requests.remove(key(realm, login)).map(_.future).getOrElse(Future.successful(false))
  }
  override def getRequest(realm: String, login: String): Option[Promise[Boolean]] =
    requests.get(key(realm, login))

  override def confirm(realm: String, login: String): Unit =
    requests.get(key(realm, login)).foreach(_.success(true))

  override def reject(realm: String, login: String): Unit =
    requests.get(key(realm, login)).foreach(_.success(false))
}
