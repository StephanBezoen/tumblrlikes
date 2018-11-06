package nl.acidcats.tumblrlikes.data_impl.likesdata

import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.LikesDataGateway
import retrofit2.HttpException
import rx.Observable
import rx.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created on 25/10/2018.
 */
class LikesDataRepositoryImpl @Inject constructor(private val likesDataGateway: LikesDataGateway) : LikesDataRepository {

    override val isLoadComplete
        get() = likesDataGateway.isLoadComplete

    override val lastLikeTime
        get() = likesDataGateway.lastLikeTimeSec

    private val tumblrLikeTransformer = TumblrLikeTransformer()

    override fun getAllLikedPhotosPages(blogName: String, afterTime: Long, loadingInterruptor: List<Boolean>, pageProgress: BehaviorSubject<Int>?): List<Photo> {
        val allPhotos: MutableList<Photo> = ArrayList()
        var time = afterTime

        do {
            likesDataGateway.getLikesPage(blogName = blogName, afterTime = time)
                    .doOnError { LoadLikesException((it as HttpException).code()) }
                    .subscribe({ likes ->
                        allPhotos.addAll(likes
                                .filter { it.isPhoto }
                                .map { tumblrLikeTransformer.transformToPhotos(it) }
                                .flatten())

                        pageProgress?.onNext(allPhotos.size)

                        time = lastLikeTime
                    }, {
                        Observable.error<Throwable>(it)
                    })
        } while (!isLoadComplete && (loadingInterruptor.isEmpty()))

        return allPhotos
    }
}