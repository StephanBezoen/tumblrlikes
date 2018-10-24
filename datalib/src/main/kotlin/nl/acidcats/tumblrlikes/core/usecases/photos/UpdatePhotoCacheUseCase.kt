package nl.acidcats.tumblrlikes.core.usecases.photos

import rx.Observable

/**
 * Created on 01/10/2018.
 */
interface UpdatePhotoCacheUseCase {
    fun removeCachedHiddenPhotos(): Observable<Boolean>

    fun checkCachedPhotos(): Observable<Int>
}
