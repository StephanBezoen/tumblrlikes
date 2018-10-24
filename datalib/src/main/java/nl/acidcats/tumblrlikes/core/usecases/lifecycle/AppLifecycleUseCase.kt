package nl.acidcats.tumblrlikes.core.usecases.lifecycle

import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface AppLifecycleUseCase {
    fun setAppStopped(currentTimeMs: Long): Observable<Boolean>

    fun isAppStoppedTooLong(currentTimeMs: Long): Observable<Boolean>
}