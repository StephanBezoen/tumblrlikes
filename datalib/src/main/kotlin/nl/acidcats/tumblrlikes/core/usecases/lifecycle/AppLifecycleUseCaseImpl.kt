package nl.acidcats.tumblrlikes.core.usecases.lifecycle

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class AppLifecycleUseCaseImpl @Inject constructor(val appDataRepository: AppDataRepository) : AppLifecycleUseCase {

    val MAX_STOP_TIME_MS = 1000L

    override fun setAppStopped(currentTimeMs: Long): Observable<Boolean> {
        appDataRepository.setAppStopTime(currentTimeMs)

        return Observable.just(true)
    }

    override fun isAppStoppedTooLong(currentTimeMs: Long): Observable<Boolean> {
        return Observable.just((currentTimeMs - appDataRepository.getAppStopTime()) > MAX_STOP_TIME_MS)
    }
}