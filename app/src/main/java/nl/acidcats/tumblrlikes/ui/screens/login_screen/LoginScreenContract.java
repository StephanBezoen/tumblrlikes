package nl.acidcats.tumblrlikes.ui.screens.login_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView;

/**
 * Created on 18/10/2018.
 */
public interface LoginScreenContract {

    int PINCODE_LENGTH = 4;
    String KEY_MODE = "key_mode";

    enum Mode {
        NEW_PINCODE, REPEAT_PINCODE, LOGIN
    }

    interface Presenter extends BasePresenter<View> {
        void onViewCreated();

        void skipLogin();

        void onPincodeInputChanged(String pincode);

        void saveState(@NonNull Bundle outState);

        void restoreState(@Nullable Bundle savedInstanceState, @Nullable Bundle args);
    }

    interface View extends BaseView {
        void setPincodeDoesntMatchViewVisible(boolean isVisible);

        void setSkipButtonVisible(boolean isVisible);

        void setHeaderTextId(@StringRes int headerTextId);

        void clearPasswordInput();
    }
}
