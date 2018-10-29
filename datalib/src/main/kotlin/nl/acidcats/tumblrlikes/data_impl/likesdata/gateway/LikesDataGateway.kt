package nl.acidcats.tumblrlikes.data_impl.likesdata.gateway

import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import rx.Observable

/**
 * Created by stephan on 28/03/2017.
 */

interface LikesDataGateway {
    fun getLikes(blogName: String, count: Int, beforeTime: Long): Observable<List<TumblrLikeVO>>

    val isLoadComplete: Boolean

    val lastLikeTimeSec : Long
}
