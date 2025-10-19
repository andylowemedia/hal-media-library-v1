package hal.media.library.v1.worker.config

import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv{
    ignoreIfMissing = true
}

object AppConfig {
    val s3Address = dotenv["S3_ADDRESS"] ?: "http://minio.low-emedia.internal:9000"
    val s3Bucket = dotenv["S3_BUCKET"] ?: "media"
    val uploadDir = dotenv["UPLOAD_DIR"] ?: "/app/uploads"
    val accessKey = dotenv["ACCESS_KEY"] ?: "media-library"
    val secretKey = dotenv["SECRET_KEY"] ?: "Alyssaj0nes"
}