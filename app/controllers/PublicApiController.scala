package controllers

import models.{ConfirmationService, RegistrationService}
import models.dto.RegistrationDTO
import play.api.mvc._

import javax.inject._
import javax.inject.Inject

@Singleton
class PublicApiController  @Inject()(val controllerComponents: ControllerComponents,
                                     reg: RegistrationService,
                                     conf: ConfirmationService) extends BaseController {

  def register(): Action[RegistrationDTO] = Action(parse.json[RegistrationDTO]){ ctx=>{
    reg.register(ctx.body) match {
      case Some(_) => Ok
      case None => BadRequest
    }
  }}

  def confirm(confirmationToken: String): Action[AnyContent] = Action(ctx=>{
    conf.confirm(confirmationToken)
    Ok
  })


  def test(confirmationToken: String): Action[AnyContent] = Action(ctx=>{
    if(conf.test(confirmationToken))
      Ok
    else
      NotFound
  })


}