package services

import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model.{ItemQuery, SearchQuery}
import com.gu.contentapi.client.model.v1._
import org.joda.time.DateTime


import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
 * Created by mmcnamara on 09/03/17.
 */
object CapiQueryService {
    val configFile = "config.conf"
    val key = LocalFileService.getCAPIKey(configFile)
    val contentApiClient = new GuardianContentClient(key)

    def getArticleBody(contentURL: String): Option[String] = {
      try {
        val searchQuery = new ItemQuery(contentURL)
          .showFields("all")
          .showElements("all")
          .showBlocks("all")
          .page(1)
          .pageSize(1)
          .orderBy("newest")

        val apiResponse = contentApiClient.getResponse(searchQuery)
        val returnedResponse = Await.result(apiResponse, (20, SECONDS))
        returnedResponse.content.flatMap(_.fields).flatMap(_.body)
      } catch {
        case _: Throwable => {
          None
        }
      }
    }

}
