package models
import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.entities.{Identity, Realm}
import models.dao.repositories.RealmRepository
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson, JwtOptions}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

import scala.concurrent.duration._
import java.time.Clock
import scala.concurrent.{Await, Future}
import scala.util.Try

@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
  def decodeDeviceToken(deviceToken: String, key: String): Option[String]

  def encodeRegistrationRequestString(request: RegistrationRequest, uri: String): Option[String]

  def encodeRegistrationRequestToken(registrationRequest: RegistrationRequest): String
  def decodeRegistrationRequestToken(token: String): Option[(RegistrationRequestToken, RegistrationRequest)]

  def decodeRequestToken(token: String): Option[RequestToken]

  def decodeConfirmationToken(token: String): Option[ConfirmationToken]
}

@Singleton
class AuthServiceImpl @Inject()(repo: RealmRepository, regRepo: RegistrationRepositoryService) extends AuthService {

  val clock: Clock = Clock.systemUTC
  override def encodeRegistrationRequestToken(registrationRequest: RegistrationRequest): String = {
    val token = RegistrationRequestToken(registrationRequest.realm, registrationRequest.login, registrationRequest.exp, registrationRequest.id)
    val claimString = Json.toJson(token).toString()
    val key = registrationRequest.key
    val algo = JwtAlgorithm.HS256
    JwtJson.encode(claimString, key, algo)
  }

  override def decodeRegistrationRequestToken(token: String): Option[(RegistrationRequestToken, RegistrationRequest)] =
    for {
      j <- JwtJson.decode(token, JwtOptions(false, true, false, 0)).toOption
      parsed <- Json.parse(j.toJson).asOpt[RegistrationRequestToken]
      request <- regRepo.get(parsed.id)
      _ <- JwtJson.decode(token, request.key, Seq[JwtHmacAlgorithm](JwtAlgorithm.HS256)).toOption
    } yield (parsed, request)

  override def decodeRequestToken(token: String): Option[RequestToken] = {
    for {
      j <- JwtJson.decode(token, JwtOptions(false, false, false, 0)).toOption
      parsed <- Json.parse(j.toJson).asOpt[RequestToken]
      realm <- Try(Await.result(repo.get(parsed.realm), Duration.Inf)).toOption.flatten
      decoded <- JwtJson.decode(token, realm.key, Seq[JwtHmacAlgorithm](JwtAlgorithm.HS256)).toOption
    } yield parsed
  }

  override def encodeRegistrationRequestString(request: RegistrationRequest, uri: String): Option[String] = {
    val t: String = encodeRegistrationRequestToken(request)
    Some(JsObject(Seq(
      "u" -> JsString(uri),
      "k" -> JsString(request.key),
      "t" -> JsString(t)
    )).toString)
  }

  override def decodeDeviceToken(deviceToken: String, key: String): Option[String] = Some(deviceToken)

  override def decodeConfirmationToken(token: String): Option[ConfirmationToken] = for {
    j <- JwtJson.decode(token, JwtOptions(false, false, false, 0)).toOption
    parsed <- Json.parse(j.toJson).asOpt[ConfirmationToken]
    realm <- Try(Await.result(repo.findWithIdentity(parsed.realm, parsed.login), Duration.Inf)).toOption.flatten
    decoded <- JwtJson.decode(token, realm._2.token, Seq[JwtHmacAlgorithm](JwtAlgorithm.HS256)).toOption
    reParsed <- Json.parse(decoded.toJson).asOpt[ConfirmationToken]
  } yield reParsed
}