package nl.acidcats.tumblrlikes.core.usecases.checktime

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class CheckTimeUseCaseImpl @Inject constructor(val appDataRepository: AppDataRepository) : CheckTimeUseCase {

    val TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L

    override fun isTimeToCheck(currentTimeInMs: Long): Observable<Boolean> {
        val timeSinceLastCheck = currentTimeInMs - appDataRepository.getLatestCheckTimestamp()
        return Observable.just(timeSinceLastCheck > TIME_BETWEEN_CHECKS_MS)
    }

    override fun resetCheckTime(): Observable<Long> {
        appDataRepository.setLatestCheckTimestamp(0L)

        return Observable.just(0L)
    }
}