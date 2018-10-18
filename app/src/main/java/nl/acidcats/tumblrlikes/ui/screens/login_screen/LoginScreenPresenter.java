package nl.acidcats.tumblrlikes.ui.screens.login_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import rx.Observable;

/**
 * Created on 18/10/2018.
 */
public class LoginScreenPresenter extends BasePresenterImpl<LoginScreenContract.View> implements LoginScreenContract.Presenter {

    @Inject
    SecurityHelper _securityHelper;
    @Inject
    PincodeUseCase _pincodeUseCase;

    private LoginScreenContract.Mode _mode;
    private String _tempPincodeHash;

    @Inject
    LoginScreenPresenter() {
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putInt(LoginScreenContract.KEY_MODE, _mode.ordinal());
    }

    @Override
    public void restoreState(@Nullable Bundle savedInstanceState, @Nullable Bundle args) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LoginScreenContract.KEY_MODE)) {
            _mode = LoginScreenContract.Mode.values()[savedInstanceState.getInt(LoginScreenContract.KEY_MODE)];
        } else if (args != null && args.containsKey(LoginScreenContract.KEY_MODE)) {
            _mode = LoginScreenContract.Mode.values()[args.getInt(LoginScreenContract.KEY_MODE)];
        }
    }

    @Override
    public void onViewCreated() {
        updateViewMode();
    }

    private void updateViewMode() {
        getView().setPincodeDoesntMatchViewVisible(false);

        switch (_mode) {
            case LOGIN:
                getView().setHeaderTextId(R.string.enter_pincode);
                getView().setSkipButtonVisible(false);
                break;
            case NEW_PINCODE:
                getView().setHeaderTextId(R.string.enter_new_pincode);
                getView().setSkipButtonVisible(true);
                break;
            case REPEAT_PINCODE:
                getView().setHeaderTextId(R.string.repeat_new_pincode);
                getView().setSkipButtonVisible(true);
                break;
        }
    }

    @Override
    public void skipLogin() {
        notify(Broadcasts.PINCODE_OK);
    }

    @Override
    public void onPincodeInputChanged(String pincode) {
        getView().setPincodeDoesntMatchViewVisible(false);

        switch (_mode) {
            case LOGIN:
                checkAuthenticated(_pincodeUseCase.checkPincode(pincode));
                break;

            case NEW_PINCODE:
                if (pincode.length() == LoginScreenContract.PINCODE_LENGTH) {
                    _tempPincodeHash = _securityHelper.getHash(pincode);

                    getView().clearPasswordInput();

                    setMode(LoginScreenContract.Mode.REPEAT_PINCODE);
                }
                break;

            case REPEAT_PINCODE:
                if (pincode.length() == LoginScreenContract.PINCODE_LENGTH) {
                    String newPincodeHash = _securityHelper.getHash(pincode);
                    if (newPincodeHash.equals(_tempPincodeHash)) {
                        checkAuthenticated(_pincodeUseCase.storePincode(pincode));
                    } else {
                        getView().clearPasswordInput();
                        getView().setPincodeDoesntMatchViewVisible(true);
                    }
                }
                break;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setMode(LoginScreenContract.Mode mode) {
        _mode = mode;

        updateViewMode();
    }

    private void checkAuthenticated(Observable<Boolean> authentication) {
        registerSubscription(
                authentication.subscribe(
                        isAuthenticated -> {
                            if (isAuthenticated) {
                                notify(Broadcasts.PINCODE_OK);
                            }
                        })
        );
    }
}
