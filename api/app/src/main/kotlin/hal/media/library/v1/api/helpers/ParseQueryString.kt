package hal.media.library.v1.api.helpers

fun parseQueryString(queryString: String): MutableMap<String, String> {
    val entries = queryString.split("&")
    val params = mutableMapOf<String, String>()
    for (entry in entries) {
        val entryData = entry.split("=")
        val key = entryData[0]
        val value = entryData[1]
        params.put(key, value)
    }
    return params
}