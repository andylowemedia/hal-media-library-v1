package hal.media.library.v1.api.handlers.libraries

import hal.media.library.v1.api.entities.MediaLibraryEntity
import hal.media.library.v1.api.handlers.entries.UpdateRequestPayload
import hal.media.library.v1.api.handlers.entries.UpdateResponse
import hal.media.library.v1.api.repositories.MediaLibraryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path

@Serializable
data class UpdateResponse(
    val success: Boolean,
    val message: String,
    val data: MediaLibraryEntity?
)

@Serializable
data class UpdateRequestPayload(
    val title: String?,
    val description: String?,
    val keywords: String?,
    val statusId: Int?,
)

fun updateHandler(request: Request): Response {
    val id: String? = request.path("id")
    if (id == null) {
        return Response(Status.BAD_REQUEST)
            .header("content-type", "application/json; charset=utf-8")
            .body(
                Json.encodeToString(
                    UpdateResponse(
                        false,
                        "No ID supplied",
                        null
                    )
                )
            )
    }
    val mediaLibrary = MediaLibraryRepository().findByFilter(mapOf("id" to id))
    if (mediaLibrary == null) {
        return Response(Status.NOT_FOUND)
    }

    val payload: UpdateRequestPayload = Json.decodeFromString(request.bodyString())

    if (payload.title != null) {
        mediaLibrary.setTitle(payload.title)
    }

    if (payload.description != null) {
        mediaLibrary.setDescription(payload.description)
    }

    MediaLibraryRepository().save(mediaLibrary, mediaLibrary.getId().toString())
    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                UpdateResponse(
                    true,
                    "Media Library Id: `${mediaLibrary.getId()}` has been updated",
                    mediaLibrary
                )
            )
        )

}