package hal.media.library.v1.worker.entities

import hal.orm.v1.entities.AbstractEntity
import kotlinx.serialization.Serializable

@Serializable
class MediaLibraryEntryEntity(): AbstractEntity<MediaLibraryEntryEntity>() {
    private var id: Int = 0
    private lateinit var code: String
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var keywords: String
    private lateinit var filename: String
    private var thumbnail: String? = null
    private lateinit var mimeType: String
    private lateinit var publishDate: String
    private var userId: Int = 0
    private var mediaLibraryId: Int = 0
    private var statusId: Int = 0
    private var mediaTypeId: Int = 0

    override fun setId(id: Int): MediaLibraryEntryEntity {
        this.id = id
        return this
    }

    override fun getId(): Int {
        return this.id
    }

    fun setCode(code: String): MediaLibraryEntryEntity {
        this.code = code
        return this
    }

    fun getCode(): String {
        return this.code
    }

    fun setTitle(title: String): MediaLibraryEntryEntity {
        this.title = title
        return this
    }

    fun getTitle(): String {
        return this.title
    }

    fun setDescription(description: String): MediaLibraryEntryEntity {
        this.description = description
        return this
    }

    fun getDescription(): String {
        return this.description
    }

    fun setKeywords(keywords: String): MediaLibraryEntryEntity {
        this.keywords = keywords
        return this
    }

    fun getKeywords(): String {
        return this.keywords
    }

    fun setFilename(filename: String): MediaLibraryEntryEntity {
        this.filename = filename
        return this
    }

    fun getFilename(): String {
        return this.filename
    }

    fun setThumbnail(thumbnail: String): MediaLibraryEntryEntity {
        this.thumbnail = thumbnail
        return this
    }

    fun getThumbnail(): String? {
        return this.thumbnail
    }

    fun setMimeType(mimeType: String): MediaLibraryEntryEntity {
        this.mimeType = mimeType
        return this
    }

    fun getMimeType(): String {
        return mimeType
    }

    fun setPublishDate(unixTimestamp: String): MediaLibraryEntryEntity {
        this.publishDate = unixTimestamp
        return this
    }

    fun getPublishDate(): String {
        return this.publishDate
    }


    fun setUserId(id: Int): MediaLibraryEntryEntity {
        this.userId = id
        return this
    }

    fun getUserId(): Int {
        return this.userId
    }

    fun setMediaLibraryId(id: Int): MediaLibraryEntryEntity {
        this.mediaLibraryId = id
        return this
    }

    fun getMediaLibraryId(): Int {
        return this.mediaLibraryId
    }

    fun setStatusId(statusId: Int): MediaLibraryEntryEntity {
        this.statusId = statusId
        return this
    }

    fun getStatusId(): Int {
        return statusId
    }

    fun setMediaTypeId(mediaTypeId: Int): MediaLibraryEntryEntity {
        this.mediaTypeId = mediaTypeId
        return this
    }

    fun getMediaTypeId(): Int {
        return this.mediaTypeId
    }
}