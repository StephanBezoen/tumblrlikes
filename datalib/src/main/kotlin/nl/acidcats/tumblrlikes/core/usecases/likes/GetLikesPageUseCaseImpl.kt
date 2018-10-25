package nl.acidcats.tumblrlikes.core.usecases.likes

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.lang.Exception
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class GetLikesPageUseCaseImpl @Inject constructor(private val appDataRepository: AppDataRepository,
                                                  private val photoDataRepository: PhotoDataRepository,
                                                  private val likesDataRepository: LikesDataRepository) : GetLikesPageUseCase {
    override fun loadLikesPage(mode: LoadLikesMode, currentTimeInMs: Long): Observable<Long> {
        val timestamp: Long = when (mode) {
            LoadLikesMode.FRESH -> currentTimeInMs
            LoadLikesMode.CONTINUED -> likesDataRepository.getLastLikeTime()
        }

        val blog = appDataRepository.getTumblrBlog() ?: return Observable.error(Exception("Blog has not been set"))

        return likesDataRepository.getLikedPhotos(blog, 20, timestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable { it }
                .filter { !photoDataRepository.hasPhoto(it.tumblrId) }
                .toList()
                .map(photoDataRepository::storePhotos)
                .map { photoDataRepository.getPhotoCount() }
    }

    override fun checkLoadLikesComplete(currentTimeInMs: Long): Observable<Boolean> {
        val isComplete = likesDataRepository.isLoadComplete() || (appDataRepository.getLatestCheckTimestamp() >= likesDataRepository.getLastLikeTime())

        if (isComplete) {
            appDataRepository.setLatestCheckTimestamp(currentTimeInMs)
        }

        return Observable.just(isComplete)
    }
}