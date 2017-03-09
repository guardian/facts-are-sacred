package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import services.LocalDynamoService

case class Response(article: String, paragraph: Int, comment: String)


class Report extends Controller {
  implicit val responseReads = Json.reads[Response]

  def submit() = Action(parse.json) {request =>
    request.body.validate[Response].map{response =>
      println(response)

    }
    Ok("wow")
  }

}