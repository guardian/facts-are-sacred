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

	def articleList() = Action {

		val comments = LocalDynamoService.allArticles
		val validComments = comments.flatMap{article =>
			article match {
				case Right(a) => Some(a)
				case _ => None
			}
		}.groupBy(_.article).mapValues(_.size).toSeq.sortWith(_._2 > _._2)

		Ok(views.html.articleList(validComments))

	}

}
