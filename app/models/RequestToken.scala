package models

import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._


case class RequestToken(realm: String, login: String, exp: Long)

object RequestToken{
  implicit val reads: Reads[RequestToken] = (
    (JsPath \ "realm").read[String] and
      (JsPath \ "login").read[String] and
      (JsPath \ "exp").read[Long])(RequestToken.apply _)
  implicit val writes: Writes[RequestToken] = (
    (JsPath \ "realm").write[String] and
      (JsPath \ "login").write[String] and
      (JsPath \ "exp").write[Long])(unlift(RequestToken.unapply)
  )
}
