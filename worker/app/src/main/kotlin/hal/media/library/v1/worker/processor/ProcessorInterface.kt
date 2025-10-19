package hal.media.library.v1.worker.processor

import com.rabbitmq.client.Channel

interface ProcessorInterface {
    abstract fun process(payload: String, headers: Map<String, Any>): Unit
}