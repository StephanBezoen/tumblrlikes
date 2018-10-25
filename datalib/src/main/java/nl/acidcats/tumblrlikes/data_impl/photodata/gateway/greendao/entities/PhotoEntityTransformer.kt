package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities

import nl.acidcats.tumblrlikes.core.models.Photo

/**
 * Created on 25/10/2018.
 */
class PhotoEntityTransformer {
    fun toPhotoEntity(photos: List<Photo>): List<PhotoEntity> {
        return photos.map { PhotoEntity(it.url, it.tumblrId) }
    }

    fun toPhoto(entity:PhotoEntity?):Photo? {
        if (entity == null) return null
        
        return Photo(
                entity.id,
                entity.photoId,
                entity.filePath,
                entity.url,
                entity.isFavorite,
                entity.isLiked,
                entity.isCached,
                entity.viewCount,
                entity.viewTime,
                entity.timePerView
        )
    }

    fun toPhotos(entities:List<PhotoEntity>?):List<Photo> {
        if (entities == null) return ArrayList()

        return entities.map { toPhoto(it)!! }
    }
}