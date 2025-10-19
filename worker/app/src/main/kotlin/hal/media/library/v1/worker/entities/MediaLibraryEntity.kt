package hal.media.library.v1.worker.entities

import hal.orm.v1.entities.AbstractEntity
import kotlinx.serialization.Serializable

//apple-system, "system-ui", "Apple Color Emoji", Inter, Roboto, "Segoe UI", "Helvetica Neue", Arial, "Noto Sans", sans-serif
//font-size64px


@Serializable
class MediaLibraryEntity(): AbstractEntity<MediaLibraryEntity>() {
    private var id: Int = 0
    private lateinit var code: String
    private lateinit var title: String
    private lateinit var description: String
    private var statusId: Int = 0

    override fun setId(id: Int): MediaLibraryEntity {
        this.id = id
        return this
    }

    override fun getId(): Int {
        return id
    }

    fun setCode(code: String): MediaLibraryEntity {
        this.code = code
        return this
    }

    fun getCode(): String {
        return this.code
    }

    fun setTitle(title: String): MediaLibraryEntity {
        this.title = title
        return this
    }

    fun getTitle(): String {
        return this.title
    }

    fun setDescription(description: String): MediaLibraryEntity {
        this.description = description
        return this
    }

    fun getDescription(): String {
        return this.description
    }

    fun setStatus(statusId: Int): MediaLibraryEntity {
        this.statusId = statusId
        return this
    }

    fun getStatusId(): Int {
        return this.statusId
    }
}