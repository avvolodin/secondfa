package controllers

import net.glxn.qrgen.javase.QRCode

import javax.inject._
import play.api._
import play.api.mvc._
import play.http.HttpEntity

@Singleton
class PrivateApiController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def checkSecondFactor(realm: String, login: String): Action[AnyContent] = Action {
    Ok(s"Realm: ${realm}, login: ${login}")
  }

  def getRegistrationToken(realm: String, login: String): Action[AnyContent] = Action {
    Ok(s"Realm: ${realm}, login: ${login}")
  }

  def getRegistrationQr(realm: String, login: String): Action[AnyContent] = Action {
    val stream = QRCode.from("https://school.vip.edu35.ru/personal-area/#marks").withSize(250,250).stream().toByteArray
    Ok(stream).as("image/png")
  }

}
