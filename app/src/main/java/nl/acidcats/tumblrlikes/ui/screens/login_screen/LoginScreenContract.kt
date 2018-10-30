package nl.acidcats.tumblrlikes.ui.screens.login_screen

import android.os.Bundle
import android.support.annotation.StringRes

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 18/10/2018.
 */
interface LoginScreenContract {

    enum class Mode {
        NEW_PINCODE, REPEAT_PINCODE, LOGIN
    }

    interface Presenter : BasePresenter<View> {
        fun onViewCreated()

        fun skipLogin()

        fun onPincodeInputChanged(pincode: String)

        fun saveState(outState: Bundle)

        fun restoreState(savedInstanceState: Bundle?, args: Bundle?)
    }

    interface View : BaseView {
        fun setPincodeDoesntMatchViewVisible(isVisible: Boolean)

        fun setSkipButtonVisible(isVisible: Boolean)

        fun setHeaderTextId(@StringRes headerTextId: Int)

        fun clearPasswordInput()
    }

    companion object {
        const val PINCODE_LENGTH = 4
        const val KEY_MODE = "key_mode"
    }
}
