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

	def createTableWithSecondaryIndex(client: AmazonDynamoDB)(tableName: String, secondaryIndexName: String)
	  	(primaryIndexAttributes: (Symbol, ScalarAttributeType)*)(secondaryIndexAttributes: (Symbol, ScalarAttributeType)*) = {

	    client.createTable(
	        new CreateTableRequest().withTableName(tableName)
	            .withAttributeDefinitions(attributeDefinitions(
	                primaryIndexAttributes.toList ++ (secondaryIndexAttributes.toList diff primaryIndexAttributes.toList)))
	            .withKeySchema(keySchema(primaryIndexAttributes))
	            .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
	            .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
	                .withIndexName(secondaryIndexName)
		            .withKeySchema(keySchema(secondaryIndexAttributes))
		            .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
		            .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
	            )
	    )

    }

	private val arbitraryThroughputThatIsIgnoredByDynamoDBLocal = new ProvisionedThroughput(1L, 1L)

	def allArticles() = {

		Scanamo.exec(client)(table.scan)

	}

	def createFeedbackTable() = {

		createTableWithSecondaryIndex(client)("feedback", "article-index")('id -> S)('article -> S)

	}

	// Feedback("1", "123", "my_article", 2, "This is wrong.")
	def putFeedback(feedback: Feedback) = {

		Scanamo.exec(client)(table.put(feedback))

	}

	def getFeedback(articleUrl: String) = {

		val articleIndex = table.index("article-index")
		Scanamo.exec(client)(articleIndex.query('article -> articleUrl))

	}

}
