package nl.acidcats.tumblrlikes.data_impl.photodata

import android.util.Log
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway
import rx.Observable
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created on 25/10/2018.
 */
class PhotoDataRepositoryImpl @Inject constructor(private val photoDataGateway: PhotoDataGateway) : PhotoDataRepository {

    private lateinit var currentFilterType: FilterType
    private var currentPhotoId: Long = 0
    private var startViewTime: Long = 0

    init {
        setFilterType(FilterType.UNHIDDEN)
    }

    override fun hasPhoto(postId: Long): Boolean = photoDataGateway.hasPhoto(postId)

    override fun storePhotos(photos: List<Photo>): List<Photo> {
        photoDataGateway.storePhotos(photos)

        return photos
    }

    override fun getAllPhotos(): List<Photo> = photoDataGateway.allPhotos

    override fun getPhotoCount(): Long = photoDataGateway.photoCount

    override fun getNextPhoto(): Photo? = photoDataGateway.nextPhoto

    override fun setPhotoViewStartTime(id: Long, currentTime: Long) {
//        Log.d(TAG, "setPhotoViewStartTime: ")

        currentPhotoId = id;

        startViewTime = currentTime;
    }

    override fun updatePhotoViewTime(id: Long, currentTime: Long) {
        if (id != 0L && id == currentPhotoId) {
            photoDataGateway.addPhotoViewTime(id, currentTime - startViewTime)
        }
    }

    override fun setPhotoLiked(id: Long, isLiked: Boolean) = photoDataGateway.setPhotoLiked(id, isLiked)

    override fun setPhotoFavorite(id: Long, isFavorite: Boolean) = photoDataGateway.setPhotoFavorite(id, isFavorite)

    override fun hidePhoto(id: Long) {
        photoDataGateway.setPhotoHidden(id)

        val photo = photoDataGateway.getPhotoById(id)
        if (photo != null) {
            uncachePhoto(photo)
        }
    }

    private fun uncachePhoto(photo: Photo): Boolean {
        photo.filePath ?: return false

        val file = File(photo.filePath)
        if (file.exists() && file.delete()) {
            photoDataGateway.setPhotoCached(photo.id, false, null)
        }

        return true
    }

    override fun getPhotoById(id: Long): Photo? = photoDataGateway.getPhotoById(id)

    override fun setFilterType(filterType: FilterType) {
        currentFilterType = filterType

        photoDataGateway.initFilter(filterType)
    }

    override fun getFilterType(): FilterType = currentFilterType

    override fun hasUncachedPhotos(): Boolean = photoDataGateway.hasUncachedPhotos()

    override fun getNextUncachedPhoto(): Photo? = photoDataGateway.uncachedPhoto

    override fun markAsCached(id: Long, path: String) = photoDataGateway.setPhotoCached(id, true, path)

    override fun removeCachedHiddenPhotos(): Observable<Boolean> {
        return Observable.fromCallable { photoDataGateway.cachedHiddenPhotos }
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it }
                .map { uncachePhoto(it) }
                .toList()
                .map { true }
    }

    override fun isPhotoCacheMissing(photo: Photo): Boolean {
        val filePath = photo.filePath ?: return false

        return !File(filePath).exists()
    }

    override fun setPhotosUncached(idList: List<Long>): List<Long> {
        photoDataGateway.setPhotosCached(idList, false)

        return idList
    }

    override fun getCachedPhotos(): List<Photo> = photoDataGateway.cachedPhotos
}