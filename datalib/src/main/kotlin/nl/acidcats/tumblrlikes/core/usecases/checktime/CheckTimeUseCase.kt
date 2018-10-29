package nl.acidcats.tumblrlikes.core.usecases.checktime

import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface CheckTimeUseCase {
    fun isTimeToCheck(currentTimeInMs: Long): Observable<Boolean>

    fun setLastCheckTime(currentTimeInMs: Long): Observable<Long>

    fun resetCheckTime(): Observable<Long>
}