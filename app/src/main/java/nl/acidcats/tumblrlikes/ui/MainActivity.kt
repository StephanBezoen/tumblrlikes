package nl.acidcats.tumblrlikes.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import nl.acidcats.tumblrlikes.LikesApplication
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.usecases.appsetup.AppSetupUseCase
import nl.acidcats.tumblrlikes.core.usecases.checktime.CheckTimeUseCase
import nl.acidcats.tumblrlikes.core.usecases.lifecycle.AppLifecycleUseCase
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase
import nl.acidcats.tumblrlikes.data.services.CacheService
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesFragment
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginFragment
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenContract
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoFragment
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment
import nl.acidcats.tumblrlikes.util.BroadcastReceiver
import java.util.*
import javax.inject.Inject

/**
 * Created on 02/11/2018.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pincodeUseCase: PincodeUseCase
    @Inject
    lateinit var checkTimeUseCase: CheckTimeUseCase
    @Inject
    lateinit var appLifecycleUseCase: AppLifecycleUseCase
    @Inject
    lateinit var appSetupUseCase: AppSetupUseCase

    private lateinit var receiver: BroadcastReceiver
    private var isRestarted: Boolean = false
    private var isStoppedTooLong: Boolean = false
    private var isShowingSetup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as LikesApplication).appComponent.inject(this)

        receiver = BroadcastReceiver(applicationContext)
        receiver.addActionHandler(Broadcasts.PINCODE_OK) { _, _ -> enterApp() }
        receiver.addActionHandler(Broadcasts.ALL_LIKES_LOADED) { _, _ -> onAllLikesLoaded() }
        receiver.addActionHandler(Broadcasts.DATABASE_RESET) { _, _ -> onDatabaseReset() }
        receiver.addActionHandler(Broadcasts.SETUP_COMPLETE) { _, _ -> onSetupComplete() }
        receiver.addActionHandler(Broadcasts.REFRESH_REQUEST) { _, _ -> onRefreshRequested() }
        receiver.addActionHandler(Broadcasts.SETTINGS_REQUEST) { _, _ -> onSettingsRequested() }

        appSetupUseCase
                .isSetupComplete()
                .subscribe { isSetupComplete ->
                    if (isSetupComplete) {
                        if (savedInstanceState == null || (application as LikesApplication).isFreshRun) {
                            checkLogin()
                        }
                    } else {
                        isShowingSetup = true

                        showFragment(SetupFragment.newInstance())
                    }
                }
    }

    override fun onRestart() {
        super.onRestart()

        isRestarted = true
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        if (!isShowingSetup) {
            if (isRestarted || isStoppedTooLong) {
                isRestarted = false
                isStoppedTooLong = false

                checkLogin()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        appLifecycleUseCase
                .isAppStoppedTooLong(System.currentTimeMillis())
                .subscribe { isStoppedTooLong = it }
    }

    override fun onResume() {
        super.onResume()

        startService(Intent(applicationContext, CacheService::class.java))

        receiver.onResume()
    }

    override fun onPause() {
        super.onPause()

        receiver.onPause()
    }

    override fun onStop() {
        super.onStop()

        appLifecycleUseCase.setAppStopped(System.currentTimeMillis()).subscribe()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun checkLogin() {
        pincodeUseCase
                .isAppPincodeProtected()
                .subscribe { isProtected ->
                    if (isProtected) {
                        showFragment(LoginFragment.newInstance(LoginScreenContract.Mode.LOGIN))
                    } else {
                        enterApp()
                    }
                }
    }

    private fun enterApp() {
        checkTimeUseCase
                .isTimeToCheck(Date().time)
                .subscribe { isTimeToCheck ->
                    showFragment(if (isTimeToCheck) LoadLikesFragment.newInstance() else PhotoFragment.newInstance())
                }
    }

    private fun onSettingsRequested() {
        showFragment(SetupFragment.newInstance())
    }

    private fun onRefreshRequested() {
        showFragment(LoadLikesFragment.newInstance())
    }

    private fun onSetupComplete() {
        isShowingSetup = false

        pincodeUseCase
                .isAppPincodeProtected()
                .subscribe { isProtected ->
                    if (isProtected) {
                        enterApp()
                    } else {
                        showFragment(LoginFragment.newInstance(LoginScreenContract.Mode.NEW_PINCODE))
                    }
                }
    }

    private fun onDatabaseReset() = checkTimeUseCase.resetCheckTime().subscribe()

    private fun onAllLikesLoaded() {
        startService(Intent(applicationContext, CacheService::class.java))

        showFragment(PhotoFragment.newInstance())
    }

    override fun onDestroy() {
        receiver.onDestroy()

        (application as LikesApplication).isFreshRun = false

        super.onDestroy()
    }
}