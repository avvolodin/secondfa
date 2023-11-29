package controllers

import models.{ConfirmationService, QrService, RegistrationService}
import net.glxn.qrgen.javase.QRCode

import javax.inject._
import play.api._
import play.api.mvc._
import play.http.HttpEntity

import scala.concurrent.ExecutionContext

@Singleton
class PrivateApiController @Inject()(val controllerComponents: ControllerComponents,
                                     val regService: RegistrationService,
                                     val qr: QrService,
                                     val con: ConfirmationService)(implicit ec: ExecutionContext) extends BaseController {
  def checkSecondFactor(requestToken: String): Action[AnyContent] = Action.async {
    con.requestConfirmation(requestToken).map(r => if(r) Ok else Unauthorized)
  }

  def getRegistrationToken(requestToken: String): Action[AnyContent] = Action {
    regService.getRegistrationTokenString(requestToken) match {
      case Some(value) => Ok(value)
      case None => BadRequest
    }
  }

  def getRegistrationQr(requestToken: String): Action[AnyContent] = Action {
    regService.getRegistrationTokenString(requestToken) match {
      case Some(value) => Ok(qr.getQrPng(value)).as("image/png")
      case None => BadRequest
    }
  }

}
