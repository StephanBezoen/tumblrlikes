package nl.acidcats.tumblrlikes.data_impl.photodata.gateway

import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.models.Photo

/**
 * Created by stephan on 11/04/2017.
 */

interface PhotoDataGateway {

    val photoCount: Long

    val nextPhoto: Photo?

    val cachedPhotos: List<Photo>

    val cachedHiddenPhotos: List<Photo>

    val uncachedPhoto: Photo?

    val allPhotos: List<Photo>

    fun hasPhoto(postId: Long): Boolean

    fun storePhotos(photos: List<Photo>)

    fun getPhotoById(id: Long): Photo?

    fun setPhotoLiked(id: Long, isLiked: Boolean)

    fun setPhotoFavorite(id: Long, isFavorite: Boolean)

    fun setPhotoHidden(id: Long)

    fun setPhotoCached(id: Long, isCached: Boolean, filepath: String?)

    fun setPhotosCached(ids: List<Long>, isCached: Boolean)

    fun addPhotoViewTime(id: Long, timeInMs: Long)

    fun initFilter(filterType: FilterType)

    fun hasUncachedPhotos(): Boolean
}
