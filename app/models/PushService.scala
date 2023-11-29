package models

import com.google.common.net.HttpHeaders
import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[PushServiceImpl])
trait PushService {
  def sendConfirmationRequest(token: String, realm: String): Future[Unit]
}

@Singleton
class PushServiceImpl @Inject()(config: Configuration, ws: WSClient)(implicit ec: ExecutionContext) extends PushService {

  val url = config.get[String]("fcm.uri")
  val serverKey =config.get[String]("fcm.serverKey")
  val senderId = config.get[String]("fcm.senderId")
  override def sendConfirmationRequest(token: String, realm: String): Future[Unit] = {
    val req = ws.url(url)
      .withHttpHeaders(HttpHeaders.AUTHORIZATION->s"key=${serverKey}",
        HttpHeaders.CONTENT_TYPE->"application/json")
    val msg = FcmMessage(token, "Confirmation request", s"Realm: ${realm}")
    val data = Json.toJson(msg)
    req.post(data).map(
      r=>if(r.status!=200){
        throw new Exception(s"Status is ${r.status} ${r.body}")
      } else ())
  }
}