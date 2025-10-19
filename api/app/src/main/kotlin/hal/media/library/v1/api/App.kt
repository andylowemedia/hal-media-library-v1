package hal.media.library.v1.api

import hal.media.library.v1.api.handlers.entries.viewHandler as entryViewHandler
import hal.media.library.v1.api.handlers.entries.updateHandler as entryUpdateHandler
import hal.media.library.v1.api.handlers.entries.deleteHandler as entryDeleteHandler
import hal.media.library.v1.api.handlers.entries.searchHandler as entrySearchHandler
import hal.media.library.v1.api.handlers.entries.uploadOptionsHandler as entryOptionsHandler
import hal.media.library.v1.api.handlers.entries.uploadHandler as entryUploadHandler
import hal.media.library.v1.api.handlers.libraries.viewHandler as libraryViewHandler
import hal.media.library.v1.api.handlers.libraries.updateHandler as libraryUpdateHandler
import hal.media.library.v1.api.handlers.libraries.deleteHandler as libraryDeleteHandler
import hal.media.library.v1.api.handlers.libraries.searchHandler as librarySearchHandler
import hal.media.library.v1.api.handlers.libraries.createHandler as libraryCreateHandler
import hal.media.library.v1.api.handlers.healthCheckHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer

fun main() {

    val entries = routes(
        "/{code}" bind Method.GET to { request: Request -> entryViewHandler(request) },
        "/{id}" bind Method.PUT to { request: Request -> entryUpdateHandler(request) },
        "/{id}" bind Method.DELETE to { request: Request -> entryDeleteHandler(request) },
        "" bind Method.GET to { request: Request -> entrySearchHandler(request) },
        "/upload" bind Method.OPTIONS to { request: Request -> entryOptionsHandler(request) },
        "/upload" bind Method.POST to { request: Request -> entryUploadHandler(request) },
    )

    val libraries = routes(
        "/{code}" bind Method.GET to { request: Request -> libraryViewHandler(request) },
        "/{id}" bind Method.PUT to { request: Request -> libraryUpdateHandler(request) },
        "/{id}" bind Method.DELETE to { request: Request -> libraryDeleteHandler(request) },
        "" bind Method.POST to { request: Request -> libraryCreateHandler(request) },
        "" bind Method.GET to { request: Request -> librarySearchHandler(request) },
    )

    val app = routes(
        "health-check" bind Method.GET to { request: Request -> healthCheckHandler() },
        "entries" bind entries,
        "libraries" bind libraries,
    )
    app.asServer(Undertow(9000)).start()
}
