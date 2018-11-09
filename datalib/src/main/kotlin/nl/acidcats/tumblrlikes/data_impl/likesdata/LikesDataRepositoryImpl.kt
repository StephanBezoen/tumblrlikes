package nl.acidcats.tumblrlikes.data_impl.likesdata

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.LikesDataGateway
import nl.acidcats.tumblrlikes.data_impl.likesdata.transformers.TransformerProvider
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

    override fun getAllLikedPhotosPages(blogName: String, afterTime: Long, loadingInterruptor: List<Boolean>, pageProgress: BehaviorSubject<Int>?): List<Photo> {
        val transformerProvider = TransformerProvider()

        val allPhotos: MutableList<Photo> = ArrayList()
        var time = afterTime

        do {
            likesDataGateway.getLikesPage(blogName = blogName, afterTime = time)
                    .doOnError { LoadLikesException((it as HttpException).code()) }
                    .subscribe({ likes ->
                        allPhotos.addAll(
                                likes
                                        .map { transformerProvider.getTransformer(it).transform(it) }
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
