package nl.acidcats.tumblrlikes.data_impl.likesdata

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.LikesDataGateway
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import retrofit2.HttpException
import rx.Observable
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

    override fun getLikedPhotos(blogName: String, count: Int, beforeTime: Long): Observable<List<Photo>> {
        return likesDataGateway.getLikes(blogName, count, beforeTime)
                .doOnError { LoadLikesException((it as HttpException).code()) }
                .flatMapIterable { it }
                .filter(TumblrLikeVO::isPhoto)
                .map(tumblrLikeTransformer::transformToPhotos)
    }
}