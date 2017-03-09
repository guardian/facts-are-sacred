package services

import java.io.FileWriter

import com.amazonaws.services.s3.model.GetObjectRequest
import com.gu.contentapi.client.model.v1.CapiDateTime
import org.joda.time.DateTime

import scala.io.Source


/**
 * Created by mmcnamara on 10/02/16.
 */
object LocalFileService {

  def getCAPIKey(fileName: String): String = {
    var contentApiKey: String = ""

    for (line <- Source.fromFile(fileName).getLines()) {
      if (line.contains("content.api.key")) {
        contentApiKey = line.takeRight((line.length - line.indexOf("=")) - 1)
      }
    }
    contentApiKey
  }
}