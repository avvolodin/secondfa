package models

import com.google.inject.{ImplementedBy, Singleton}

import java.time.Clock
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.crypto.KeyGenerator
import scala.collection.mutable


@ImplementedBy(classOf[RegistrationRepositoryServiceImpl])
trait RegistrationRepositoryService {
  def create(realm: String, login: String): Option[RegistrationRequest]
  def get(id: String): Option[RegistrationRequest]
  def getByRealmAndLogin(realm: String, login: String): Option[RegistrationRequest]
  def drop(id: String): Unit
}

@Singleton
class RegistrationRepositoryServiceImpl extends RegistrationRepositoryService {

  val lifetime: Long = TimeUnit.MINUTES.toMillis(30)

  val requests: mutable.Map[String, RegistrationRequest] =
    scala.collection.mutable.Map[String, RegistrationRequest]()

  val gen: KeyGenerator = KeyGenerator.getInstance("HmacSHA256")
  val b64: Base64.Encoder = Base64.getEncoder

  val clock: Clock = Clock.systemUTC

  private def newRequest(realm:String, login: String)={
    val id = java.util.UUID.randomUUID().toString
    RegistrationRequest(id, realm, login, b64.encodeToString(gen.generateKey().getEncoded), clock.millis() + lifetime)
  }

  override def create(realm: String, login: String): Option[RegistrationRequest] = {
    requests.values.find(v => v.login == login && v.realm == realm) match {
      case Some(x) => if(x.exp>=clock.millis()){
        requests.remove(x.id)
        val r = newRequest(realm, login)
        requests(r.id) = r
        Some(r)
      } else {
        None
      }

      case None => {
        val req = newRequest(realm, login)
        requests(req.id) = req
        Some(req)
      }
    }
  }

  override def get(id: String): Option[RegistrationRequest] =
    requests.get(id).flatMap(r =>
      if(r.exp>clock.millis())
        Some(r)
      else {
        requests.remove(id)
        None
      })

  override def getByRealmAndLogin(realm: String, login: String): Option[RegistrationRequest] =
    requests.values.find(r=>r.login==login && r.realm == realm)
      .flatMap(r =>
        if (r.exp < clock.millis())
          Some(r)
        else {
          requests.remove(r.id)
          None
        })

  override def drop(id: String): Unit = {
    requests.remove(id)
  }
}
