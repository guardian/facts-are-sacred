package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import services.LocalDynamoService
import models.Feedback

case class Response(article: String, paragraph: Int, comment: String)


class Report extends Controller {
  implicit val responseReads = Json.reads[Response]

  def submit() = Action(parse.json) {request =>

    request.body.validate[Response].map{response =>

    	val id = java.util.UUID.randomUUID.toString
    	val feedback = Feedback(id, response.article, "2", response.paragraph, response.comment)

      LocalDynamoService.putFeedback(feedback)

    }

    Ok("wow")

  }

}