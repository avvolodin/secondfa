package controllers

import play.api.mvc._
import javax.inject._
import javax.inject.Inject

@Singleton
class PublicApiController  @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def register(): Action[AnyContent] = Action(ctx=>{
    Ok
  })

  def confirm(): Action[AnyContent] = Action(ctx=>{
    Ok
  })

  def test(): Action[AnyContent] = Action(ctx=>{
    Ok
  })


}