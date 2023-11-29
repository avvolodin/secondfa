package models.dto

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._

case class RegistrationDTO(deviceToken: String, token: String)

object RegistrationDTO {
  implicit val reads: Reads[RegistrationDTO] = (
    (JsPath \ "deviceToken").read[String] and
      (JsPath \ "token").read[String])(RegistrationDTO.apply _)
  implicit val writes: Writes[RegistrationDTO] = (
    (JsPath \ "deviceToken").write[String] and
      (JsPath \ "token").write[String])(unlift(RegistrationDTO.unapply)
  )
}
