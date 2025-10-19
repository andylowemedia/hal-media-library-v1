package hal.media.library.v1.worker.processor

import com.rabbitmq.client.Channel
import hal.media.library.v1.worker.config.AppConfig
import hal.media.library.v1.worker.entities.MediaLibraryEntity
import hal.media.library.v1.worker.entities.MediaLibraryEntryEntity
import hal.media.library.v1.worker.repositories.MediaLibraryEntryRepository
import hal.media.library.v1.worker.repositories.MediaLibraryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.util.stream.Stream
import kotlin.concurrent.thread
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ConversionPayload(
    val file: String,
    val userId: String,
    val mediaLibraryId: String,
    val title: String,
    val description: String,
    val keywords: String,
)

@Serializable
data class VideoStream(
    val codec_name: String,
    val width: Int,
    val height: Int,
    val r_frame_rate: String,
    val bit_rate: String,
)

@Serializable
data class VideoFormat(
    val duration: String,
    val bit_rate: String,
)


@Serializable
data class VideoMetadata(
    val programs: List<String>,
    val streams: List<VideoStream>,
    val format: VideoFormat,
)
class FileProcessor() : ProcessorInterface {
    override fun process(payload: String, headers: Map<String, Any>): Unit {
        try {
            val conversionPayload: ConversionPayload = Json.decodeFromString(payload)
            val nameParts = conversionPayload.file.split(".")
            val settings = this.runFfprobe(conversionPayload.file, AppConfig.uploadDir)
            this.runFfmpegSimple(conversionPayload.file, "${nameParts[0]}.m3u8", AppConfig.uploadDir)
            this.uploadFileToS3(nameParts[0], conversionPayload.userId)
            Thread.sleep(1000)
            this.cleanOutHlsDirectory(File(AppConfig.uploadDir  ), nameParts[0])
            this.saveRecord(conversionPayload, settings, nameParts[0])
        } catch (error:Exception) {
            println("**********************************************")
            println("An error has occurred")
            if ("kotlinx.serialization.json.internal.JsonDecodingException" == error.javaClass.canonicalName) {
                println("JSON payload can not be parsed")
            }
            println(error)
            println(error.printStackTrace())
            println("headers=${headers}")
            println("payload=${payload}")
            println("**********************************************")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveRecord(conversionPayload: ConversionPayload, settings: VideoMetadata, targetFile: String) {
        val entity = MediaLibraryEntryEntity()
        entity.setCode(Uuid.random().toString())
            .setTitle(conversionPayload.title)
            .setDescription(conversionPayload.description)
            .setKeywords(conversionPayload.keywords)
            .setFilename("/${targetFile}/${targetFile}.m3u8")
            .setMimeType("application/vnd.apple.mpegurl")
            .setPublishDate(System.currentTimeMillis().toString())
            .setUserId(conversionPayload.userId.toInt())
            .setMediaLibraryId(conversionPayload.mediaLibraryId.toInt())
            .setMediaTypeId(1)
            .setStatusId(1)
        MediaLibraryEntryRepository()
            .save(entity)
    }

    private fun runFfprobe(input: String, workingDir: String): VideoMetadata {
        val command = listOf(
            "ffprobe",
            "-v", "error",
            "-select_streams",
            "v:0",
            "-show_entries",
            "stream=width,height,r_frame_rate,codec_name,bit_rate",
            "-show_entries",
            "format=duration,bit_rate",
            "-of", "json",
            input
        )
        val process = ProcessBuilder(command)
            .directory(File(workingDir)) // <-- sets working directory
            .redirectErrorStream(true)
            .start()
        val jsonOutput = process.inputStream.bufferedReader().readText()
        return Json.decodeFromString(jsonOutput)
    }

    private fun runFfmpegSimple(input: String, output: String, workingDir: String) {
        val command = listOf(
            "ffmpeg",
            "-i", input,
            "-vf", "scale=-2:1080",
            "-c:v", "libx264",
            "-preset", "veryfast",
            "-crf", "23",
            "-c:a", "aac",
            "-b:a", "128k",
            "-ac", "2",
            "-f", "hls",
            "-hls_time", "6",
            "-hls_list_size", "0",
            "-hls_playlist_type", "vod",
            output
        )

        println("Starting FFmpeg process in directory: $workingDir")

        val process = ProcessBuilder(command)
            .directory(File(workingDir)) // <-- sets working directory
            .inheritIO()  // let FFmpeg print output directly
            .start()

        val exitCode = process.waitFor()
        val file = File("${workingDir}/${input}");
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                println("File deleted successfully.")
            } else {
                println("Failed to delete the file.")
            }
        } else {
            println("File does not exist.")
        }
        println("FFmpeg finished with exit code $exitCode")
        println("Output file: $output")
        if (exitCode != 0) {
            throw Exception("Error during process exited with non-zero exit code.")
        }
    }

    private fun uploadFileToS3(filename: String, userId: String) {
        val accessKey = AppConfig.accessKey
        val secretKey = AppConfig.secretKey
        val minioEndpoint = AppConfig.s3Address
        val bucket = AppConfig.s3Bucket

        // Create S3 client pointing to MinIO
        val s3 = S3Client.builder()
            .endpointOverride(URI.create(minioEndpoint))
            .region(Region.US_EAST_1) // Region is ignored by MinIO, but required
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .forcePathStyle(true)
            .build()
        uploadHlsDirectory(s3, bucket, File(AppConfig.uploadDir), "/video/${userId}/$filename", filename)
        println("File uploaded successfully to MinIO!")

        // Close the client
        s3.close()
    }

    private fun uploadHlsDirectory(s3: S3Client, bucket: String, localDir: File, s3Prefix: String, filename: String) {
        Files.walk(localDir.toPath()).forEach { path ->
            println(filename)
            if (Files.isRegularFile(path) && path.toString().contains(filename)) {
                val file = path.fileName.toString()
                val s3Key = "$s3Prefix/$file"
                println("Uploading $s3Key")
                val contentType = when(path.toFile().extension) {
                    "ts" -> "video/MP2T"
                    "m3u8" -> "application/vnd.apple.mpegurl"
                    else -> "application/octet-stream"
                }

                val request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(contentType)
                    .build()

                s3.putObject(request, RequestBody.fromFile(path.toFile()))
                println("Uploaded $s3Key")
            } else {
                println("skipping $path")
            }
        }
    }

    private fun cleanOutHlsDirectory(localDir: File, filename: String) {
        Files.walk(localDir.toPath()).forEach { path ->
            if (Files.isRegularFile(path) && path.toString().contains(filename)) {
                val file = path.toFile()
                file.delete()
                println("Deleted $path")
            } else {
                println("skipping $path")
            }
        }
    }
}
