package hal.media.library.v1.worker.repositories

import hal.media.library.v1.worker.config.SqlConfig
import hal.media.library.v1.worker.entities.MediaLibraryEntryEntity
import hal.orm.v1.repositories.AbstractMySqlRepository
import kotlin.reflect.KFunction0

class MediaLibraryEntryRepository(): AbstractMySqlRepository<MediaLibraryEntryEntity>(
    SqlConfig.host,
    SqlConfig.port,
    SqlConfig.user,
    SqlConfig.password,
    SqlConfig.type,
    SqlConfig.schema,
) {
    override var tableName = "media_library_entry"
    override var entityClassName = "hal.media.library.v1.worker.entities.MediaLibraryEntryEntity"
    override var entityClassInvokeable: KFunction0<MediaLibraryEntryEntity>? = ::MediaLibraryEntryEntity
    override var externalColumnList: List<String> = emptyList()
}