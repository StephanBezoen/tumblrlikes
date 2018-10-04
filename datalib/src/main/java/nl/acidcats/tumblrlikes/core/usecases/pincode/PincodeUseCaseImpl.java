package nl.acidcats.tumblrlikes.core.usecases.pincode;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import rx.Observable;

/**
 * Created on 28/09/2018.
 */
public class PincodeUseCaseImpl implements PincodeUseCase {

    private AppDataRepository _appDataRepository;
    private SecurityHelper _securityHelper;

    @Inject
    public PincodeUseCaseImpl(AppDataRepository appDataRepository, SecurityHelper securityHelper) {
        _appDataRepository = appDataRepository;
        _securityHelper = securityHelper;
    }

    @Override
    public Observable<Boolean> checkPincode(String pincode) {
        boolean isCorrect;

        if (_appDataRepository.getPincodeHash() != null) {
            String pinCodeHash = _securityHelper.getHash(pincode);
            String storedPinCodeHash = _appDataRepository.getPincodeHash();
            isCorrect = pinCodeHash.equals(storedPinCodeHash);
        } else {
            isCorrect = true;
        }

        return Observable.just(isCorrect);
    }

    @Override
    public Observable<Boolean> storePincode(String pincode) {
        _appDataRepository.setPincodeHash(_securityHelper.getHash(pincode));

        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> isAppPincodeProtected() {
        return Observable.just(_appDataRepository.getPincodeHash() != null);
    }
}
