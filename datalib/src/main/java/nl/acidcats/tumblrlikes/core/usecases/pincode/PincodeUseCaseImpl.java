package nl.acidcats.tumblrlikes.core.usecases.pincode;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import rx.Observable;

/**
 * Created on 28/09/2018.
 */
public class PincodeUseCaseImpl implements PincodeUseCase {

    private AppDataRepository _appDataRepository;

    @Inject
    public PincodeUseCaseImpl(AppDataRepository appDataRepository) {
        _appDataRepository = appDataRepository;
    }


    @Override
    public Observable<Boolean> checkPincode(String pincode) {
        return Observable.just(_appDataRepository.isPincodeCorrect(pincode));
    }

    @Override
    public Observable<Boolean> storePincode(String pincode) {
        _appDataRepository.setPincode(pincode);

        return Observable.just(true);
    }
}
