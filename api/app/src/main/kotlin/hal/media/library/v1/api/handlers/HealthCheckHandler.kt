package hal.media.library.v1.api.handlers

import org.http4k.core.Response
import org.http4k.core.Status

fun healthCheckHandler(): Response {
    return Response(Status.OK)
        .header("content-type", "application/json; charset=utf-8")
        .body("{\"message\":\"Media library microservice health check\"}")
}
