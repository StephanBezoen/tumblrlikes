package nl.acidcats.tumblrlikes.core.usecases.appsetup

import rx.Observable

/**
 * Created on 24/10/2018.
 */
interface AppSetupUseCase {
    fun isSetupComplete(): Observable<Boolean>
}