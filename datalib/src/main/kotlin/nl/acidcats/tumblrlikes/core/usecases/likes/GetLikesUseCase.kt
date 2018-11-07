package nl.acidcats.tumblrlikes.core.usecases.likes

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import rx.Observable
import rx.subjects.BehaviorSubject

/**
 * Created on 24/10/2018.
 */
interface GetLikesUseCase {
    fun loadAllLikes(mode: LoadLikesMode, loadingInterruptor: List<Boolean>, currentTimeInMs: Long, pageProgress: BehaviorSubject<Int>? = null): Observable<Int>
}