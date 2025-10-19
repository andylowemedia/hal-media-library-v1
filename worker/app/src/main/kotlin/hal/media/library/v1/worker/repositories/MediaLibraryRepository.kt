package hal.media.library.v1.worker.repositories

import hal.media.library.v1.worker.config.SqlConfig
import hal.media.library.v1.worker.entities.MediaLibraryEntity
import hal.orm.v1.repositories.AbstractMySqlRepository
import kotlin.reflect.KFunction0

class MediaLibraryRepository(): AbstractMySqlRepository<MediaLibraryEntity>(
    SqlConfig.host,
    SqlConfig.port,
    SqlConfig.user,
    SqlConfig.password,
    SqlConfig.type,
    SqlConfig.schema,
) {
    override var tableName = "media_library"
    override var entityClassName = "hal.media.library.v1.worker.entities.MediaLibraryEntity"
    override var entityClassInvokeable: KFunction0<MediaLibraryEntity>? = ::MediaLibraryEntity
    override var externalColumnList: List<String> = emptyList()
}