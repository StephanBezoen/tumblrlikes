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

    override var isLoadComplete: Boolean = false
        private set
    override var lastLikeTime: Long = 0
        private set

    private val tumblrLikeTransformer = TumblrLikeTransformer()

    override fun getLikedPhotos(blogName: String, count: Int, beforeTime: Long): Observable<List<Photo>> {
        return likesDataGateway.getLikes(blogName, count, beforeTime)
                .doOnError { LoadLikesException((it as HttpException).code()) }
                .map(this::checkHasMore)
                .flatMapIterable { it }
                .filter(TumblrLikeVO::isPhoto)
                .map(tumblrLikeTransformer::transformToPhotos)
    }

    private fun checkHasMore(likes: List<TumblrLikeVO>): List<TumblrLikeVO> {
        if (likes.isEmpty()) {
            isLoadComplete = true
        } else {
            isLoadComplete = false

            lastLikeTime = likes.last().timestamp
        }

        return likes
    }
}