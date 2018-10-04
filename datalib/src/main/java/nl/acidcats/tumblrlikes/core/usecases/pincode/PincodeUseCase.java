package nl.acidcats.tumblrlikes.core.usecases.pincode;


import rx.Observable;

/**
 * Created on 28/09/2018.
 */
public interface PincodeUseCase {
    Observable<Boolean> checkPincode(String pincode);

    Observable<Boolean> storePincode(String pincode);

    Observable<Boolean> isAppPincodeProtected();
}
