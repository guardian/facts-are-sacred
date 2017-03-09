package controllers

import play.api.mvc.{Controller, Action}

import models.Feedback
import services.LocalDynamoService

class Application extends Controller {

	def viewArticleFeedback(id: String) = Action {		

		val result = LocalDynamoService.getFeedback(id)
		Ok(views.html.index(result))

	}
	
}
