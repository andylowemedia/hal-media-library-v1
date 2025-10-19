package hal.media.library.v1.worker

import hal.media.library.v1.worker.config.QueueConfig
import hal.media.library.v1.worker.config.SimpleQueueConfig
import hal.media.library.v1.worker.processor.FileProcessor
import hal.media.library.v1.worker.queue.RabbitMqQueue
import java.io.File
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import java.net.URI
import java.time.Duration

fun main() {
    println("loading Queue")
//    RabbitMqQueue(
//        QueueConfig.host,
//        QueueConfig.port,
//        QueueConfig.user,
//        QueueConfig.pass,
//        QueueConfig.ssl,
//        QueueConfig.queue
//    ).process(
//        FileProcessor()
//    )

    val sqsClient = SqsClient.builder()
        .endpointOverride(URI.create(SimpleQueueConfig.host))
        .region(Region.US_EAST_1) // ignored by ElasticMQ
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create("x", "x"))
        )
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .build()

    val queueName = SimpleQueueConfig.queue

    // Create queue if it doesn't exist
    val queueUrl = try {
        sqsClient.getQueueUrl { it.queueName(queueName) }.queueUrl()
    } catch (e: QueueDoesNotExistException) {
        sqsClient.createQueue { it.queueName(queueName) }.queueUrl()
    }

    println("Listening to queue: $queueUrl")

    val processor = FileProcessor()

    // Sequential worker loop
    while (true) {
        val messages = sqsClient.receiveMessage {
            it.queueUrl(queueUrl)
            it.maxNumberOfMessages(1)       // one message at a time
            it.waitTimeSeconds(20)          // long polling
            it.visibilityTimeout(86400)      // 1-hour visibility for long jobs
        }.messages()

        if (messages.isEmpty()) continue

        for (message in messages) {
            try {
                println("Processing message: ${message.body()} (ID: ${message.messageId()})")

                // Simulate video processing / transcoding
                println(message.body())
                println(message.attributes())
                processor.process(message.body(), message.attributes() as Map<String, Any>)

                // Delete message after successful processing
                sqsClient.deleteMessage {
                    it.queueUrl(queueUrl)
                    it.receiptHandle(message.receiptHandle())
                }
                println("Finished and deleted message: ${message.messageId()}")
            } catch (ex: Exception) {
                println("Failed processing message: ${message.messageId()}, error: ${ex.message}")
                // Message will become visible again after visibility timeout for retry
            }
        }
    }
}



