package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Writes}

case class FcmMessage(token: String, title: String, body: String, key:String = "")

object FcmMessage {
  implicit val writes: Writes[FcmMessage] = (
    (JsPath \ "to").write[String] and
      (JsPath \ "notification" \ "title").write[String] and
      (JsPath \ "notification" \ "body").write[String] and
      (JsPath \ "data" \ "key").write[String]) (unlift(FcmMessage.unapply)
  )
}
