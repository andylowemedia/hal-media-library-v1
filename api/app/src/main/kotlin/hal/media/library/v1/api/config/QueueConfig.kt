package hal.media.library.v1.api.config

object QueueConfig {
    val host = dotenv["QUEUE_HOST"] ?: "media-library-rabbitmq"
    val port = (dotenv["QUEUE_PORT"] ?: "5672").toInt()
    val user = dotenv["QUEUE_USER"] ?: "guest"
    val pass = dotenv["QUEUE_PASS"] ?: "guest"
    val ssl =  (dotenv["QUEUE_SSL"] ?: "false").lowercase() === "true"
    val queue = dotenv["QUEUE_QUEUE"] ?: "hal-media-library-conversion"
}

object SimpleQueueConfig {
    val host = dotenv["SIMPLE_QUEUE_HOST"] ?: "http://media-library-elasticmq:9324"
    val queue = dotenv["SIMPLE_QUEUE_NAME"] ?: "hal-media-library-conversion"
}