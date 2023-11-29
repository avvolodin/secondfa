package models

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.repositories.RealmRepository

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ConfirmationServiceImpl])
trait ConfirmationService {
  def test(confirmationToken: String): Boolean

  def confirm(confirmationToken: String): Unit

  def requestConfirmation(requestToken: String): Future[Boolean]

}

@Singleton
class ConfirmationServiceImpl @Inject()(
                                       auth: AuthService,
                                       pushService: PushService,
                                       reqRep: RequestRepositoryService,
                                       realmRepo: RealmRepository
                                       )(implicit ec: ExecutionContext) extends ConfirmationService {
  override def confirm(confirmationToken: String): Unit = {
    auth.decodeConfirmationToken(confirmationToken).foreach( token =>
      if(token.result) reqRep.confirm(token.realm, token.login) else reqRep.reject(token.realm, token.login)
    )
  }

  override def test(confirmationToken: String): Boolean =
    auth.decodeConfirmationToken(confirmationToken).isDefined

  override def requestConfirmation(requestToken: String): Future[Boolean] = {
    for {
      t <- auth.decodeRequestToken(requestToken) match {
        case Some(v) => Future.successful(v)
        case None => Future.failed(new Throwable())
      }
      o <- realmRepo.findWithIdentity(t.realm, t.login)
      (realm, identity) <- o match {
        case Some(value) => Future.successful(value)
        case None => Future.failed(new Throwable())
      }
      _ <- pushService.sendConfirmationRequest(identity.token, realm.id)
      f <- reqRep.addRequest(t.realm, t.login)
    } yield f
  }
}
