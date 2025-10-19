package hal.media.library.v1.api.repositories

import hal.media.library.v1.api.config.SqlConfig
import hal.media.library.v1.api.entities.MediaLibraryEntity
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
    override var entityClassName = "hal.media.library.v1.api.entities.MediaLibraryEntity"
    override var entityClassInvokeable: KFunction0<MediaLibraryEntity>? = ::MediaLibraryEntity
    override var externalColumnList: List<String> = emptyList()
}