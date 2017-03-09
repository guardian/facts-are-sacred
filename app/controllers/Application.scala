package controllers

import play.api.mvc.{Controller, Action}

import models.Feedback
import services._


class Application extends Controller {

  val configFile = "config.conf"
  val capiKey = LocalFileService.getCAPIKey(configFile)

	def viewArticleFeedback(id: String) = Action {		

		val result = LocalDynamoService.getFeedback(id)
		Ok(views.html.index(result))

	}

}
