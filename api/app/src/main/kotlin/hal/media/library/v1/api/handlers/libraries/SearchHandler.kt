package hal.media.library.v1.api.handlers.libraries

import hal.media.library.v1.api.entities.MediaLibraryEntity
import hal.media.library.v1.api.helpers.parseQueryString
import hal.media.library.v1.api.repositories.MediaLibraryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

@Serializable
data class SearchResponse(
    val success: Boolean,
    val message: String,
    val data: MutableList<MediaLibraryEntity>
)

fun searchHandler(request: Request): Response {
    val queryString = request.uri.toString().split("?")[1]
    val params = parseQueryString(queryString)

    val mediaLibraryList = MediaLibraryRepository().findAll(params)

    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body(
            Json.encodeToString(
                SearchResponse(
                    true,
                    "Found `${mediaLibraryList.count()}` media library objects",
                    mediaLibraryList
                )
            )
        )
}