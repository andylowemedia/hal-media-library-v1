package hal.media.library.v1.api.handlers.entries

import hal.media.library.v1.api.entities.MediaLibraryEntryEntity
import hal.media.library.v1.api.helpers.parseQueryString
import hal.media.library.v1.api.repositories.MediaLibraryEntryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

@Serializable
data class SearchResponse(
    val success: Boolean,
    val message: String,
    val data: MutableList<MediaLibraryEntryEntity>
)

fun searchHandler(request: Request): Response {
    val queryString = request.uri.toString().split("?")[1]
    val params = parseQueryString(queryString)

    val mediaList = MediaLibraryEntryRepository().findAll(params)

    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                SearchResponse(
                    true,
                    "Found `${mediaList.count()}` media objects",
                    mediaList
                )
            )
        )
}
