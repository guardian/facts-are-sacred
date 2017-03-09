package controllers

import play.api.mvc.{Controller, Action}

import models.Feedback
import services.CapiQueryService.ArticleContent
import services.{CapiQueryService,LocalDynamoService,LocalFileService}


class Application extends Controller {
  
	def viewArticleFeedback(articleUrl: String) = Action {

		val result = LocalDynamoService.getFeedback(articleUrl)
		Ok(views.html.index("<p>An article of stuff</p>", result))

	}

}
