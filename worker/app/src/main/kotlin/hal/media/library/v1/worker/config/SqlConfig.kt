package hal.media.library.v1.worker.config

object SqlConfig {
    val host = dotenv["SQL_HOST"] ?: "media-library-sql"
    val port = dotenv["SQL_PORT"] ?: "3306"
    val user = dotenv["SQL_USER"] ?: "root"
    val password = dotenv["SQL_PASSWORD"] ?: "admin"
    val type = dotenv["SQL_TYPE"] ?: "mysql"
    val schema = dotenv["SQL_DATABASE"] ?: "hal-media-library"
}
