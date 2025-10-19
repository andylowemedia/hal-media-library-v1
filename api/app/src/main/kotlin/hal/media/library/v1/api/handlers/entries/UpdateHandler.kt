package hal.media.library.v1.api.handlers.entries

import hal.media.library.v1.api.entities.MediaLibraryEntryEntity
import hal.media.library.v1.api.repositories.MediaLibraryEntryRepository
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
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
    val data: MediaLibraryEntryEntity?
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
    val media = MediaLibraryEntryRepository().findByFilter(mapOf("id" to id))
    if (media == null) {
        return Response(Status.NOT_FOUND)
    }

    val payload: UpdateRequestPayload = Json.decodeFromString(request.bodyString())

    if (payload.title != null) {
        media.setTitle(payload.title)
    }

    if (payload.description != null) {
        media.setDescription(payload.description)
    }

    if (payload.keywords != null) {
        media.setKeywords(payload.keywords)
    }

    MediaLibraryEntryRepository().save(media, media.getId().toString())
    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                UpdateResponse(
                    true,
                    "Media Id: `${media.getId()}` has been updated",
                    media
                )
            )
        )
}
