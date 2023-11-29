package models

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.repositories.RealmRepository
import models.dto.RegistrationDTO
import play.api.Configuration

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try


@ImplementedBy(classOf[RegistrationServiceImpl])
trait RegistrationService {
    def getRegistrationTokenString(requestToken: String): Option[String]
    def register(reg: RegistrationDTO): Option[String]
}

@Singleton
class RegistrationServiceImpl @Inject()(auth: AuthService,
                                        qr: QrService,
                                        repo: RealmRepository,
                                        regRep: RegistrationRepositoryService,
                                        config: Configuration) extends RegistrationService {
  override def getRegistrationTokenString(requestToken: String): Option[String] = for {
    // decode and test token
    t <- auth.decodeRequestToken(requestToken)
    // create request
    request <- regRep.create(t.realm, t.login)
    uri <- config.getOptional[String]("mobile.uri")
    resp <- auth.encodeRegistrationRequestString(request, uri)
  } yield resp

  override def register(reg: RegistrationDTO): Option[String] = for {
    (rt, rr) <- auth.decodeRegistrationRequestToken(reg.token)
    deviceToken <- auth.decodeDeviceToken(reg.deviceToken, rr.key)
    _ <- Try(Await.result(repo.setIdentity(rr.realm, rr.login, deviceToken), Duration.Inf)).toOption
  } yield rr.login
}