package nl.acidcats.tumblrlikes.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.Lazy
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
import nl.acidcats.tumblrlikes.util.broadcast.BroadcastReceiver
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper
import java.util.*
import javax.inject.Inject

/**
 * Created on 02/11/2018.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pincodeUseCase: Lazy<PincodeUseCase>
    @Inject
    lateinit var checkTimeUseCase: Lazy<CheckTimeUseCase>
    @Inject
    lateinit var appLifecycleUseCase: Lazy<AppLifecycleUseCase>
    @Inject
    lateinit var appSetupUseCase: Lazy<AppSetupUseCase>
    @Inject
    lateinit var permissionHelper: Lazy<PermissionHelper>

    private lateinit var receiver: BroadcastReceiver
    private var isRestarted: Boolean = false
    private var isStoppedTooLong: Boolean = false
    private var isShowingSetup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as LikesApplication).appComponent.inject(this)

        receiver = BroadcastReceiver(applicationContext)
        receiver.addActionHandler(Broadcasts.PINCODE_OK) { enterApp() }
        receiver.addActionHandler(Broadcasts.ALL_LIKES_LOADED) { showPhotoScreen(false) }
        receiver.addActionHandler(Broadcasts.DATABASE_RESET) { onDatabaseReset() }
        receiver.addActionHandler(Broadcasts.SETUP_COMPLETE) { onSetupComplete() }
        receiver.addActionHandler(Broadcasts.REFRESH_REQUEST) { onRefreshRequested() }
        receiver.addActionHandler(Broadcasts.SETTINGS_REQUEST) { onSettingsRequested() }
        receiver.addActionHandler(Broadcasts.CACHE_SERVICE_REQUEST) { startCacheService() }

        appSetupUseCase.get()
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

        appLifecycleUseCase.get()
                .isAppStoppedTooLong(System.currentTimeMillis())
                .subscribe { isStoppedTooLong = it }
    }

    override fun onResume() {
        super.onResume()

        receiver.onResume()
    }

    override fun onPause() {
        super.onPause()

        receiver.onPause()
    }

    override fun onStop() {
        super.onStop()

        appLifecycleUseCase.get().setAppStopped(System.currentTimeMillis()).subscribe()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun checkLogin() {
        pincodeUseCase.get()
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
        checkTimeUseCase.get()
                .isTimeToCheck(Date().time)
                .subscribe { isTimeToCheck ->
                    showPhotoScreen(isTimeToCheck)
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

        pincodeUseCase.get()
                .isAppPincodeProtected()
                .subscribe { isProtected ->
                    if (isProtected) {
                        enterApp()
                    } else {
                        showFragment(LoginFragment.newInstance(LoginScreenContract.Mode.NEW_PINCODE))
                    }
                }
    }

    private fun onDatabaseReset() = checkTimeUseCase.get().resetCheckTime().subscribe()

    private fun showPhotoScreen(refreshLikes: Boolean) {
        startCacheService()

        showFragment(PhotoFragment.newInstance(refreshLikes))
    }

    private fun startCacheService() {
        startService(Intent(applicationContext, CacheService::class.java))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionHelper.get().onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        receiver.onDestroy()

        (application as LikesApplication).clearFreshRun()

        super.onDestroy()
    }
}