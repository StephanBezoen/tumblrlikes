package nl.acidcats.tumblrlikes.core.repositories

import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.models.Photo
import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface PhotoDataRepository {
    fun hasPhoto(postId: Long): Boolean

    fun storePhotos(photos: List<Photo>): List<Photo>

    fun getAllPhotos(): List<Photo>

    fun getPhotoCount(): Long

    fun getNextPhoto(): Photo?

    fun setPhotoViewStartTime(id: Long, currentTime: Long)

    fun updatePhotoViewTime(id: Long, currentTime: Long)

    fun setPhotoLiked(id: Long, isLiked: Boolean)

    fun setPhotoFavorite(id: Long, isFavorite: Boolean)

    fun hidePhoto(id: Long)

    fun getPhotoById(id: Long): Photo?

    fun setFilterType(filterType: FilterType)

    fun getFilterType(): FilterType

    fun hasUncachedPhotos(): Boolean

    fun getNextUncachedPhoto(): Photo?

    fun markAsCached(id: Long, path: String)

    fun removeCachedHiddenPhotos(): Observable<Boolean>

    fun isPhotoCacheMissing(photo: Photo): Boolean

    fun setPhotosUncached(idList: List<Long>): List<Long>

    fun getCachedPhotos(): List<Photo>
}