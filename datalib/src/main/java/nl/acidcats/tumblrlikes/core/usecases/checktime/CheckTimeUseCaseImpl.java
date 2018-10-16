package nl.acidcats.tumblrlikes.core.usecases.checktime;

import java.util.Date;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import rx.Observable;

/**
 * Created on 16/10/2018.
 */
public class CheckTimeUseCaseImpl implements CheckTimeUseCase {

    private static final long TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L; // 24 hours

    private AppDataRepository _appDataRepository;

    @Inject
    public CheckTimeUseCaseImpl(AppDataRepository appDataRepository) {
        _appDataRepository = appDataRepository;
    }

    @Override
    public Observable<Boolean> isTimeToCheck() {
        long timeSinceLastCheck = new Date().getTime() - _appDataRepository.getLatestCheckTimestamp();
        return Observable.just(timeSinceLastCheck > TIME_BETWEEN_CHECKS_MS);
    }

    @Override
    public Observable<Long> resetCheckTime() {
        _appDataRepository.setLatestCheckTimestamp(0L);

        return Observable.just(0L);
    }
}
