package hal.media.library.v1.api.handlers.entries

import hal.media.library.v1.api.entities.MediaLibraryEntryEntity
import hal.media.library.v1.api.repositories.MediaLibraryEntryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path

@Serializable
data class ViewResponse(
    val success: Boolean,
    val message: String,
    val data: MediaLibraryEntryEntity?
)

fun viewHandler(request: Request): Response {
    val code: String? = request.path("code")
    if (code == null) {
        return Response(Status.BAD_REQUEST)
            .header("content-type", "application/json; charset=utf-8")
            .body(
                Json.encodeToString(
                    ViewResponse(
                        false,
                        "No ID has been defined",
                        null
                    )
                )
            )
    }


    val media = MediaLibraryEntryRepository().findByFilter(mapOf<String, String>("code" to code, "status_id" to "1"))
    if (media == null) {
        return Response(Status.NOT_FOUND)
    }

    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                ViewResponse(
                    true,
                    "Found media code: `${code}`",
                    media,
                )
            )
        )
}
