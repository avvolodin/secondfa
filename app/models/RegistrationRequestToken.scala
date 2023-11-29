package models

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class RegistrationRequestToken(realm: String, login: String, exp: Long, id: String)
object RegistrationRequestToken {
  implicit val reads: Reads[RegistrationRequestToken] = (
    (JsPath \ "realm").read[String] and
    (JsPath \ "login").read[String] and
    (JsPath \ "exp").read[Long] and
    (JsPath \ "id").read[String])(RegistrationRequestToken.apply _)
  implicit val writes: Writes[RegistrationRequestToken] = (
    (JsPath \ "realm").write[String] and
      (JsPath \ "login").write[String] and
      (JsPath \ "exp").write[Long] and
      (JsPath \ "id").write[String])(unlift(RegistrationRequestToken.unapply)
  )
}
