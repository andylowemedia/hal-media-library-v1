package hal.media.library.v1.api.handlers.entries

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import hal.media.library.v1.api.config.SimpleQueueConfig
import hal.media.library.v1.api.entities.MediaLibraryEntryEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.MultipartFormBody
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException
import java.net.URI

@Serializable
data class MessagePayload(
    val file: String,
    val userId: String,
    val mediaLibraryId: String,
    val title: String,
    val description: String,
    val keywords: String,
)

@OptIn(ExperimentalUuidApi::class)
fun uploadHandler(request: Request): Response {
    val receivedForm = MultipartFormBody.from(request)
    val title = receivedForm.fieldValue("title").toString()
    val description = receivedForm.fieldValue("description").toString()
    val keywords = receivedForm.fieldValue("keywords").toString()
    val mediaLibraryId = receivedForm.fieldValue("mediaLibraryId").toString()
    val userId = receivedForm.fieldValue("userId").toString()

    val filePart = receivedForm.file("filename")
    val originalFilename = filePart?.filename

    val filenameParts = originalFilename.toString().split(".")
    val filename = "${Uuid.random()}.${filenameParts[filenameParts.count() - 1]}"
    val path = "/app/uploads"
    val fullFilename = "$path/$filename"
    println(fullFilename)


    val targetFile = File(fullFilename)
    targetFile.createNewFile()

    val fileStream = filePart!!.content
    var outStream: OutputStream? = null
    var inputStream: InputStream? = null

    try {
        inputStream = fileStream
        outStream = FileOutputStream(targetFile)

        val buffer = ByteArray(8 * 1024 * 1024) // 8 MB buffer
        var bytesRead = inputStream.read(buffer)
        while (bytesRead != -1) {
            outStream.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }
        outStream.flush()
    } finally {
        if (inputStream != null) {
            try { inputStream.close() } catch (_: Exception) {}
        }
        if (outStream != null) {
            try { outStream.close() } catch (_: Exception) {}
        }
    }

    println(SimpleQueueConfig.host)

    val sqsClient = SqsClient.builder()
        .endpointOverride(URI.create(SimpleQueueConfig.host))
        .region(Region.US_EAST_1) // required but ignored by ElasticMQ
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("x", "x") // dummy credentials
            )
        )
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .build()

    // 2️⃣ Create a queue
    val queueName = SimpleQueueConfig.queue

    val queueUrl = try {
        sqsClient.getQueueUrl { it.queueName(queueName) }.queueUrl()
    } catch (e: QueueDoesNotExistException) {
        sqsClient.createQueue { it.queueName(queueName) }.queueUrl()
    }

//    val createQueueResponse = sqsClient.createQueue(
//        CreateQueueRequest.builder()
//            .queueName(queueName)
//            .build()
//    )
//    val queueUrl = createQueueResponse.queueUrl()

    println("Queue URL: $queueUrl")
    val messagePayload = MessagePayload(
        filename,
        userId,
        mediaLibraryId,
        title,
        description,
        keywords,
    )

        val sendRequest = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(Json.encodeToString(messagePayload))
        .build()
    val sendResponse = sqsClient.sendMessage(sendRequest)
    println("Sent message ID: ${sendResponse.messageId()}")



//    val factory = ConnectionFactory()
//    factory.host = QueueConfig.host
//    factory.port = QueueConfig.port
//    factory.username = QueueConfig.user
//    factory.password = QueueConfig.pass
//    if (QueueConfig.ssl) {
//        factory.useSslProtocol()
//    }
//
//
//    val connection = factory.newConnection()
//    val channel = connection.createChannel()
//
//    val messagePayload = MessagePayload(
//        filename,
//        "",
//        ""
//    )
//    val message = Json.encodeToString(messagePayload)
//    try {
//        // Declare queue
//        channel.queueDeclare(QueueConfig.queue, true, false, false, null)
//        // Publish message
//        channel.basicPublish(
//            "",
//            QueueConfig.queue,
//            MessageProperties.PERSISTENT_TEXT_PLAIN,
//            message.toByteArray(Charsets.UTF_8)
//        )
//        println("Sent message to queue '${QueueConfig.queue}': $message")
//    } finally {
//        channel.close()
//        connection.close()
//    }
//
//
//    val bucketName = "uploads";
//
//    val credentials: AWSCredentials = BasicAWSCredentials("adminuser", "adminuser")
//
//    System.out.format("Uploading %s to S3 bucket %s...\n", fullFilename, bucketName)
//    val s3 = AmazonS3ClientBuilder
//        .standard()
//        .withEndpointConfiguration(
//            AwsClientBuilder.EndpointConfiguration(S3Config.host, Regions.DEFAULT_REGION.toString()))
//        .withCredentials(AWSStaticCredentialsProvider(credentials))
//        .withPathStyleAccessEnabled(true)
//        .build()
//
//    val unixTime = System.currentTimeMillis()
//
//    val data = MediaLibraryEntity()
//        .setProfileId(receivedForm.fieldValue("profile-id").toString().toInt())
//        .setTag(receivedForm.fieldValue("tag").toString())
//        .setType(receivedForm.fieldValue("type").toString().toInt())
//        .setMimeType(receivedForm.fieldValue("mime-type").toString())
//        .setUnixTimestamp(unixTime.toString())
//        .setStatusId(3)
//        .setFile(filename)
//    MediaLibraryRepository().save(data)
//
//    try {
//        s3.putObject("$bucketName/user-id", filename, File(fullFilename))
//    } catch (e: AmazonServiceException) {
//        System.err.println(e.getErrorMessage())
//        System.exit(1)
//    }

    //publicToQueue(data)

    return Response(Status.OK).header("Access-Control-Allow-Origin", "*")
}

fun uploadOptionsHandler(request: Request): Response {
    return Response(Status.OK)
        .header("Access-Control-Allow-Origin", "*")
}

fun publicToQueue(data: MediaLibraryEntryEntity) {
    val factory = ConnectionFactory()

    factory.isAutomaticRecoveryEnabled = false

    val queueName = "hal-file-processing"
    factory.host = ""
    factory.port = 0
    factory.username = ""
    factory.password = ""

    val ssl = false
    if (ssl) {
        factory.useSslProtocol()
    }

    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    val headers: Map<String, String> = mapOf(
        "media-library-id" to "${data.getId()}",
    )
    val headerString = AMQP.BasicProperties.Builder()
        .headers(headers)
        .build()
    println("Publish to queue: $queueName")
    channel.basicPublish("", queueName, headerString, "".toByteArray(StandardCharsets.UTF_8))

}