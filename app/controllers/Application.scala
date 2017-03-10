package controllers

import play.api.mvc.{Action, Controller}
import models.Feedback
import services.CapiQueryService.ArticleContent
import services.{CapiQueryService, LocalDynamoService, LocalFileService}
import java.net.URLDecoder

import play.api.libs.json.Json


class Application extends Controller {
	implicit val feedwrite = Json.writes[Feedback]
	def viewArticleFeedback(articleUrl: String) = Action {

		val decodedUrl = URLDecoder.decode(articleUrl, "UTF-8")

		val results = LocalDynamoService.getFeedback(decodedUrl)
		val article = CapiQueryService.getArticleContent(decodedUrl)

		val comments = results.map{result =>
			result match {
				case Right(success) => Some(success)
				case _ => None
			}
		}.flatten



		article match {
			case Some(content) => Ok(views.html.index(content, Json.toJson(comments)))
			case None => Ok("Article not found")
		}

	}

}
