package nl.acidcats.tumblrlikes.core.usecases.lifecycle;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public class AppLifecycleUseCaseImpl implements AppLifecycleUseCase {

    private static final long MAX_STOP_TIME_MS = 1000L;

    private AppDataRepository _appDataRepository;

    @Inject
    public AppLifecycleUseCaseImpl(AppDataRepository appDataRepository) {
        _appDataRepository = appDataRepository;
    }

    @Override
    public Observable<Boolean> setAppStopped(long currentTimeMs) {
        _appDataRepository.setAppStopTime(currentTimeMs);

        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> isAppStoppedTooLong(long currentTimeMs) {
        return Observable.just((currentTimeMs - _appDataRepository.getAppStopTime()) > MAX_STOP_TIME_MS);
    }
}
