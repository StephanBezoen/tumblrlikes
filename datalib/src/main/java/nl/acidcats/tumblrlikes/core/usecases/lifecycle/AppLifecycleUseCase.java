package nl.acidcats.tumblrlikes.core.usecases.lifecycle;

import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public interface AppLifecycleUseCase {

    Observable<Boolean> setAppStopped(long currentTimeMs);

    Observable<Boolean> isAppStoppedTooLong(long currentTimeMs);
}
