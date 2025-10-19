package hal.media.library.v1.api.handlers.entries

import hal.media.library.v1.api.repositories.MediaLibraryEntryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path

@Serializable
data class DeleteResponse(
    val success: Boolean,
    val message: String,
)

fun deleteHandler(request: Request): Response {
    val id = request.path("id")
    if (id == null) {
        return Response(Status.BAD_REQUEST)
            .header("content-type", "application/json; charset=utf-8")
            .body(Json.encodeToString(DeleteResponse(false, "No ID supplied")))
    }
    val media = MediaLibraryEntryRepository().findByFilter(mapOf<String, String>("id" to id))
    if (media == null) {
        return Response(Status.NOT_FOUND)
    }
    media.setStatusId(2)
    MediaLibraryEntryRepository().save(media)

    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(Json.encodeToString(DeleteResponse(true, "Media ID: `${id}` Marked as Deleted")))
}