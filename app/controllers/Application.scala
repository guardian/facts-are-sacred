package controllers

import play.api.mvc.{Controller, Action}

import models.Feedback
import services.LocalDynamoService

class Application extends Controller {

	def viewArticleFeedback(articleUrl: String) = Action {		

		val result = LocalDynamoService.getFeedback(articleUrl)
		Ok(views.html.index(result))

	}
	
}
