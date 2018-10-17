package nl.acidcats.tumblrlikes.core.usecases.checktime;


import rx.Observable;

/**
 * Created on 16/10/2018.
 */
public interface CheckTimeUseCase {

    Observable<Boolean> isTimeToCheck(long currentTimeInMs);

    Observable<Long> resetCheckTime();
}
