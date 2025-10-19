package hal.media.library.v1.api.entities

import hal.orm.v1.entities.AbstractEntity
import kotlinx.serialization.Serializable

@Serializable
class MediaLibraryEntity(): AbstractEntity<MediaLibraryEntity>() {
    private var id: Int = 0
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

    fun setStatusId(statusId: Int): MediaLibraryEntity {
        this.statusId = statusId
        return this
    }

    fun getStatusId(): Int {
        return this.statusId
    }
}