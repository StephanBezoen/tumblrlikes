package nl.acidcats.tumblrlikes.core.usecases.appsetup;

import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public interface AppSetupUseCase {
    Observable<Boolean> isSetupComplete();
}
