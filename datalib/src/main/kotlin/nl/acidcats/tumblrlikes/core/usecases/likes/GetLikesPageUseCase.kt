package nl.acidcats.tumblrlikes.core.usecases.likes

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface GetLikesPageUseCase {
    fun loadLikesPage(mode: LoadLikesMode): Observable<Long>

    fun checkLoadLikesComplete(): Observable<Boolean>
}