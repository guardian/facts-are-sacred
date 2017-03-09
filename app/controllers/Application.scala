package controllers

import play.api.mvc.{Controller, Action}

import models.Feedback
import services.CapiQueryService.ArticleContent
import services.{CapiQueryService,LocalDynamoService,LocalFileService}

import java.net.URLDecoder


class Application extends Controller {
  
	def viewArticleFeedback(articleUrl: String) = Action {

		val decodedUrl = URLDecoder.decode(articleUrl, "UTF-8")

		val result = LocalDynamoService.getFeedback(decodedUrl)
		val article = CapiQueryService.getArticleContent(decodedUrl)

		article match {
			case Some(content) => Ok(views.html.index(content, result))
			case None => Ok("Article not found")
		}

	}

}
