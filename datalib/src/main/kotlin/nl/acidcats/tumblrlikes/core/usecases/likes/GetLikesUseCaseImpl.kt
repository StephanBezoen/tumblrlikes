package nl.acidcats.tumblrlikes.core.usecases.likes

import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.Observable.error
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class GetLikesUseCaseImpl @Inject constructor(private val appDataRepository: AppDataRepository,
                                              private val photoDataRepository: PhotoDataRepository,
                                              private val likesDataRepository: LikesDataRepository) : GetLikesUseCase {

    override fun loadAllLikes(mode: LoadLikesMode,
                              loadingInterruptor: List<Boolean>,
                              currentTimeInMs: Long,
                              pageProgress: BehaviorSubject<Int>?): Observable<Int> {
        val timestamp: Long = when (mode) {
            LoadLikesMode.RELOAD_ALL -> 1
            LoadLikesMode.SINCE_LAST -> Math.max(1L, appDataRepository.getLastLikeTime())
        }

        val blog = appDataRepository.getTumblrBlog() ?: return error(Exception("Blog has not been set"))

        return Observable
                .fromCallable { likesDataRepository.getAllLikedPhotosPages(blog, timestamp, loadingInterruptor, pageProgress) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { isNew(it) }
                .map(photoDataRepository::storePhotos)
                .map { loadedPhotos ->
                    Timber.d { "loadAllLikes: ${loadedPhotos.size} photos stored" }

                    if (likesDataRepository.lastLikeTime != 0L) {
                        appDataRepository.setLastLikeTime(likesDataRepository.lastLikeTime)
                    }

                    appDataRepository.setLastCheckTime(currentTimeInMs)

                    loadedPhotos.size
                }
    }

    private fun isNew(photos: List<Photo>): List<Photo> {
        return photos.filter { !photoDataRepository.hasPhoto(it.tumblrId) }
    }
}