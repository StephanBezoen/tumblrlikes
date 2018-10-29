package nl.acidcats.tumblrlikes.core.repositories

import nl.acidcats.tumblrlikes.core.models.Photo
import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface LikesDataRepository {
    fun getLikedPhotos(blogName: String, count: Int, beforeTime: Long): Observable<List<Photo>>

    val isLoadComplete: Boolean

    val lastLikeTime: Long
}