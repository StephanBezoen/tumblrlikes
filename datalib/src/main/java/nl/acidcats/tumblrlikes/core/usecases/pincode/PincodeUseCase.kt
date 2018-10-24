package nl.acidcats.tumblrlikes.core.usecases.pincode


import rx.Observable

/**
 * Created on 28/09/2018.
 */
interface PincodeUseCase {

    fun isAppPincodeProtected(): Observable<Boolean>

    fun checkPincode(pincode: String): Observable<Boolean>

    fun storePincode(pincode: String): Observable<Boolean>
}
