package nl.acidcats.tumblrlikes.ui.screens.login_screen

import android.os.Bundle
import nl.acidcats.tumblrlikes.R.string
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenContract.Mode.*
import nl.acidcats.tumblrlikes.util.security.SecurityHelper
import rx.Observable
import javax.inject.Inject
import dagger.Lazy

/**
 * Created on 30/10/2018.
 */
class LoginScreenPresenter @Inject constructor() : BasePresenterImpl<LoginScreenContract.View>(), LoginScreenContract.Presenter {

    @Inject
    lateinit var securityHelper: Lazy<SecurityHelper>
    @Inject
    lateinit var pincodeUseCase: Lazy<PincodeUseCase>

    private lateinit var tempPincodeHash: String
    private var mode = LOGIN

    override fun onViewCreated() {
        updateViewMode()
    }

    private fun updateViewMode() {
        getView()?.setPincodeDoesntMatchViewVisible(false)

        when (mode) {
            LOGIN -> {
                getView()?.setHeaderTextId(string.enter_pincode)
                getView()?.setSkipButtonVisible(false)
            }
            NEW_PINCODE -> {
                getView()?.setHeaderTextId(string.enter_new_pincode)
                getView()?.setSkipButtonVisible(true)
            }
            REPEAT_PINCODE -> {
                getView()?.setHeaderTextId(string.repeat_new_pincode)
                getView()?.setSkipButtonVisible(true)
            }
        }
    }

    override fun skipLogin() = notify(Broadcasts.PINCODE_OK)

    override fun onPincodeInputChanged(pincode: String) {
        getView()?.setPincodeDoesntMatchViewVisible(false)

        when (mode) {
            LOGIN -> checkAuthenticated(pincodeUseCase.get().checkPincode(pincode))
            NEW_PINCODE -> checkNewPincode(pincode)
            REPEAT_PINCODE -> checkRepeatPincode(pincode)
        }
    }

    private fun checkRepeatPincode(pincode: String) {
        if (pincode.length == LoginScreenContract.PINCODE_LENGTH) {
            val newPincodeHash = securityHelper.get().getHash(pincode)
            if (newPincodeHash == tempPincodeHash) {
                checkAuthenticated(pincodeUseCase.get().storePincode(pincode))
            } else {
                getView()?.clearPasswordInput()
                getView()?.setPincodeDoesntMatchViewVisible(true)
            }
        }
    }

    private fun checkNewPincode(pincode: String) {
        if (pincode.length == LoginScreenContract.PINCODE_LENGTH) {
            tempPincodeHash = securityHelper.get().getHash(pincode)

            getView()?.clearPasswordInput()

            mode = REPEAT_PINCODE
            updateViewMode()
        }
    }

    private fun checkAuthenticated(authenticator: Observable<Boolean>) {
        registerSubscription(
                authenticator.subscribe { isAuthenticated ->
                    if (isAuthenticated) {
                        notify(Broadcasts.PINCODE_OK)
                    }
                }
        )
    }

    override fun saveState(outState: Bundle) {
        outState.putString(LoginScreenContract.KEY_MODE, mode.name)
    }

    override fun restoreState(savedInstanceState: Bundle?, args: Bundle?) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LoginScreenContract.KEY_MODE)) {
            mode = valueOf(savedInstanceState.getString(LoginScreenContract.KEY_MODE)!!)
        } else if (args != null && args.containsKey(LoginScreenContract.KEY_MODE)) {
            mode = valueOf(args.getString(LoginScreenContract.KEY_MODE)!!)
        }
    }
}