package nl.acidcats.tumblrlikes.core.usecases.likes

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.Observable.error
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class GetLikesPageUseCaseImpl @Inject constructor(private val appDataRepository: AppDataRepository,
                                                  private val photoDataRepository: PhotoDataRepository,
                                                  private val likesDataRepository: LikesDataRepository) : GetLikesPageUseCase {
    override fun loadLikesPage(mode: LoadLikesMode): Observable<Long> {
        val timestamp: Long = when (mode) {
            LoadLikesMode.RELOAD_ALL -> 1
            LoadLikesMode.SINCE_LAST -> Math.max(1L, appDataRepository.getLastLikeTime())
            LoadLikesMode.NEXT_PAGE -> likesDataRepository.lastLikeTime
        }

        val blog = appDataRepository.getTumblrBlog() ?: return error(Exception("Blog has not been set"))

        return likesDataRepository.getLikedPhotos(blog, 20, timestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable { it }
                .filter { !photoDataRepository.hasPhoto(it.tumblrId) }
                .toList()
                .map(photoDataRepository::storePhotos)
                .map {
                    if (likesDataRepository.lastLikeTime != 0L) {
                        appDataRepository.setLastLikeTime(likesDataRepository.lastLikeTime)
                    }

                    photoDataRepository.getPhotoCount()
                }
    }

    override fun checkLoadLikesComplete(): Observable<Boolean> = Observable.just(likesDataRepository.isLoadComplete)
}