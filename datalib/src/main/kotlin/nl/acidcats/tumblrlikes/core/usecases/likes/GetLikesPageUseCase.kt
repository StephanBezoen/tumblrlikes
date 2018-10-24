package nl.acidcats.tumblrlikes.core.usecases.likes

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface GetLikesPageUseCase {
    fun loadLikesPage(mode: LoadLikesMode, currentTimeInMs: Long): Observable<Long>

    fun checkLoadLikesComplete(currentTimeInMs: Long): Observable<Boolean>
}