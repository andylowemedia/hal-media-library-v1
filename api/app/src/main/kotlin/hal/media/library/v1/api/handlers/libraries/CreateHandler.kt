package hal.media.library.v1.api.handlers.libraries

import hal.media.library.v1.api.entities.MediaLibraryEntity
import hal.media.library.v1.api.handlers.entries.ViewResponse
import hal.media.library.v1.api.repositories.MediaLibraryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

data class CreateMediaLibraryRequest(
    val title: String,
    val description: String,
)

@Serializable
data class CreateResponse(
    val success: Boolean,
    val message: String,
    val data: MediaLibraryEntity?
)

fun createHandler(request: Request): Response {
    val payload: CreateMediaLibraryRequest = Json.decodeFromString(request.bodyString())

    val mediaLibrary = MediaLibraryEntity()
    mediaLibrary.setTitle(payload.title).setDescription(payload.description).setStatusId(1)
    MediaLibraryRepository().save(mediaLibrary)

    return Response(Status.CREATED)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                CreateResponse(
                    true,
                    "Created media library: `${mediaLibrary.getId()}`",
                    mediaLibrary,
                )
            )
        )
}