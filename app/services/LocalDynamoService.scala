package services

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import com.gu.scanamo._
import com.gu.scanamo.syntax._
import collection.JavaConverters._

import models.Feedback

object LocalDynamoService {

	lazy val client = getClient
	val table = Table[Feedback]("feedback")

	private def getClient(): AmazonDynamoDBAsync =
	    AmazonDynamoDBAsyncClient.asyncBuilder()
	    	.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "credentials")))
	    	.withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", ""))
	    	.build()

	private def attributeDefinitions(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    	attributes.map{ case (symbol, attributeType) => new AttributeDefinition(symbol.name, attributeType)}.asJava
	}

	private def keySchema(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
	    val hashKeyWithType :: rangeKeyWithType = attributes.toList
	    val keySchemas = hashKeyWithType._1 -> KeyType.HASH :: rangeKeyWithType.map(_._1 -> KeyType.RANGE)
	    keySchemas.map{ case (symbol, keyType) => new KeySchemaElement(symbol.name, keyType)}.asJava
	}

	private val arbitraryThroughputThatIsIgnoredByDynamoDBLocal = new ProvisionedThroughput(1L, 1L)

	def createFeedbackTable() = {
		client.createTable(
	    	attributeDefinitions(Seq('id -> S)),
	    	"feedback",
	    	keySchema(Seq('id -> S)),
	    	arbitraryThroughputThatIsIgnoredByDynamoDBLocal
	    )
	}

	// Feedback("1", "123", "my_article", 2, "This is wrong.")
	def putFeedback(feedback: Feedback) = {
		Scanamo.exec(client)(table.put(feedback))
	}

	def getFeedback(id: String) = Scanamo.exec(client)(table.get('id -> id))

}
