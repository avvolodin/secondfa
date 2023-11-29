package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

case class ConfirmationToken(realm: String, login: String, exp: Long, result: Boolean)

object ConfirmationToken{
  implicit val reads: Reads[ConfirmationToken] = (
    (JsPath \ "realm").read[String] and
      (JsPath \ "login").read[String] and
      (JsPath \ "exp").read[Long] and
      (JsPath \ "result").read[Boolean])(ConfirmationToken.apply _)

  implicit val writes: Writes[ConfirmationToken] = (
    (JsPath \ "realm").write[String] and
      (JsPath \ "login").write[String] and
      (JsPath \ "exp").write[Long] and
      (JsPath \ "result").write[Boolean])(unlift(ConfirmationToken.unapply)
  )
}