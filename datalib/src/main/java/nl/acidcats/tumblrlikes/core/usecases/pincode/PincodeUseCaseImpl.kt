package nl.acidcats.tumblrlikes.core.usecases.pincode

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.util.security.SecurityHelper
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class PincodeUseCaseImpl @Inject constructor(private val appDataRepository: AppDataRepository,
                                             private val securityHelper: SecurityHelper) : PincodeUseCase {

    override fun isAppPincodeProtected(): Observable<Boolean> {
        return Observable.just(appDataRepository.getPincodeHash() != null)
    }

    override fun checkPincode(pincode: String): Observable<Boolean> {
        appDataRepository.getPincodeHash() ?: return Observable.just(true)

        val pinCodeHash = securityHelper.getHash(pincode)
        val storedPinCodeHash = appDataRepository.getPincodeHash()

        return Observable.just(storedPinCodeHash == pinCodeHash)
    }

    override fun storePincode(pincode: String): Observable<Boolean> {
        appDataRepository.setPincodeHash(securityHelper.getHash(pincode))

        return Observable.just(true)
    }
}