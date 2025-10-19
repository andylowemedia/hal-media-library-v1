package hal.media.library.v1.worker.queue

import com.rabbitmq.client.*
import com.rabbitmq.client.impl.DefaultExceptionHandler
import hal.media.library.v1.worker.processor.ProcessorInterface
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.toMap


val exceptionHandler: ExceptionHandler = object : DefaultExceptionHandler() {
    override fun handleConsumerException(
        channel: Channel,
        exception: Throwable?,
        consumer: Consumer?,
        consumerTag: String?,
        methodName: String?
    ) {
        if (exception is Throwable) {
            println(exception.message)
            channel.close()
            System.exit(0)
        }
    }
}

class RabbitMqQueue (
    private val host: String,
    private val port: Int,
    private val user: String,
    private val pass: String,
    private val ssl: Boolean,
    private val queueName: String
) {
    fun process(processor: ProcessorInterface) {
        DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val factory = ConnectionFactory()

        factory.isAutomaticRecoveryEnabled = false
        factory.setExceptionHandler(exceptionHandler)

        val queueName = this.queueName
        factory.host = this.host
        factory.port = this.port
        factory.username = this.user
        factory.password = this.pass

        if (this.ssl) {
            factory.useSslProtocol()
        }

        val connection: Connection = factory.newConnection()
        val channel: Channel = connection.createChannel()

        channel.queueDeclare(queueName, true, false, false, null)
        println(" [*] Waiting for messages. To exit press CTRL+C")
        println("")
        channel.basicQos(1)
        channel.basicConsume(queueName, false, this.loopingMessages(channel, processor), this.cancelCallback())
    }

    private fun loopingMessages(channel: Channel, processor: ProcessorInterface) : DeliverCallback {
        return DeliverCallback { _: String?, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
//            val headers: Map<String, Any> = delivery.properties.headers as Map<String, Any>
            val startedDate = LocalDateTime.now().toString()
            println("************************************************")
            println("${startedDate} [x] Received")
            processor.process(message, emptyMap())
            channel.basicAck(delivery.envelope.deliveryTag, false)
            val endedDate = LocalDateTime.now().toString()
            println("${endedDate} [x] Processed")
            println("************************************************")
            println("")
        }

    }

    private fun cancelCallback() : CancelCallback {
        return CancelCallback { consumerTag: String? ->
            println("[$consumerTag] was canceled")
        }
    }
}