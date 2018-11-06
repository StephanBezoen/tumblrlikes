package nl.acidcats.tumblrlikes.core.repositories

import nl.acidcats.tumblrlikes.core.models.Photo
import rx.subjects.BehaviorSubject

/**
 * Created on 24/10/2018.
 */
interface LikesDataRepository {
    fun getAllLikedPhotosPages(blogName: String, afterTime: Long, loadingInterruptor: List<Boolean>, pageProgress: BehaviorSubject<Int>?): List<Photo>

    val isLoadComplete: Boolean

    val lastLikeTime: Long
}