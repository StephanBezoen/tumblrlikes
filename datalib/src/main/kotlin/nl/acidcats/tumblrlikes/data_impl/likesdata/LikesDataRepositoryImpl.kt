package nl.acidcats.tumblrlikes.data_impl.likesdata

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.LikesDataGateway
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
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
        var hasError = false

        do {
            likesDataGateway.getLikesPage(blogName = blogName, afterTime = time)
                    .doOnError {
                        hasError = true
                        throw LoadLikesException((it as HttpException).code())
                    }
                    .subscribe({ likes ->
                        allPhotos.addAll(transformLikesToPhotos(likes, transformerProvider))

                        pageProgress?.onNext(allPhotos.size)

                        time = lastLikeTime
                    }, {
                        throw it
                    })
        } while (!isLoadComplete && !hasError && (loadingInterruptor.isEmpty()))

        return allPhotos
    }

    private fun transformLikesToPhotos(likes: List<TumblrLikeVO>, transformerProvider: TransformerProvider): List<Photo> {
        val photosLists = likes.map {
            transformerProvider.getTransformer(it).transform(it)
        }
        return photosLists.flatten()
    }
}
