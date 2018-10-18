package nl.acidcats.tumblrlikes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.usecases.appsetup.AppSetupUseCase;
import nl.acidcats.tumblrlikes.core.usecases.checktime.CheckTimeUseCase;
import nl.acidcats.tumblrlikes.core.usecases.lifecycle.AppLifecycleUseCase;
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase;
import nl.acidcats.tumblrlikes.data.services.CacheService;
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginFragment;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoFragment;
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment;
import nl.acidcats.tumblrlikes.util.BroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    FirebaseAnalytics _analytics;
    @Inject
    PincodeUseCase _pincodeUseCase;
    @Inject
    CheckTimeUseCase _checkTimeUseCase;
    @Inject
    AppLifecycleUseCase _appLifecycleUseCase;
    @Inject
    AppSetupUseCase _appSetupUseCase;

    private BroadcastReceiver _receiver;
    private boolean _isRestarted;
    private boolean _isStoppedTooLong;
    private boolean _isShowingSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LikesApplication) getApplication()).getAppComponent().inject(this);

        _receiver = new BroadcastReceiver(this);
        _receiver.addActionHandler(Broadcasts.PASSWORD_OK, this::onPasswordOk);
        _receiver.addActionHandler(Broadcasts.ALL_LIKES_LOADED, this::onAllLikesLoaded);
        _receiver.addActionHandler(Broadcasts.DATABASE_RESET, this::onDatabaseReset);
        _receiver.addActionHandler(Broadcasts.SETUP_COMPLETE, this::onSetupComplete);
        _receiver.addActionHandler(Broadcasts.REFRESH_REQUEST, this::onRefreshRequest);
        _receiver.addActionHandler(Broadcasts.SETTINGS_REQUEST, this::onSettingsRequest);

        _appSetupUseCase
                .isSetupComplete()
                .subscribe(isSetupComplete -> {
                    if (isSetupComplete) {
                        if (savedInstanceState == null || ((LikesApplication) getApplication()).isFreshRun()) {
                            checkLogin();
                        }
                    } else {
                        _isShowingSetup = true;

                        showFragment(SetupFragment.newInstance());
                    }
                });
    }

    private void onSettingsRequest(String action, Intent intent) {
        showFragment(SetupFragment.newInstance());
    }

    private void onRefreshRequest(String action, Intent intent) {
        showFragment(LoadLikesFragment.newInstance());
    }

    private void checkLogin() {
        _pincodeUseCase.isAppPincodeProtected().subscribe(
                isPincodeProtected -> {
                    if (isPincodeProtected) {
                        showFragment(LoginFragment.newInstance(LoginFragment.Mode.LOGIN));
                    } else {
                        enterApp();
                    }
                }
        );
    }

    private void onSetupComplete(String action, Intent intent) {
        _isShowingSetup = false;

        _pincodeUseCase.isAppPincodeProtected().subscribe(
                isPincodeProtected -> {
                    if (isPincodeProtected) {
                        enterApp();
                    } else {
                        showFragment(LoginFragment.newInstance(LoginFragment.Mode.NEW_PINCODE));
                    }
                }
        );
    }

    private void onDatabaseReset(String action, Intent intent) {
        _checkTimeUseCase.resetCheckTime().subscribe();
    }

    private void onAllLikesLoaded(String action, Intent intent) {
        startService(new Intent(this, CacheService.class));

        showFragment(PhotoFragment.newInstance());
    }

    private void onPasswordOk(String action, Intent intent) {
        enterApp();
    }

    private void enterApp() {
        _checkTimeUseCase.isTimeToCheck(new Date().getTime()).subscribe(
                isTimeToCheck -> showFragment(isTimeToCheck ? LoadLikesFragment.newInstance() : PhotoFragment.newInstance())
        );
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        _isRestarted = true;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (!_isShowingSetup) {
            if (_isRestarted || _isStoppedTooLong) {
                _isRestarted = false;
                _isStoppedTooLong = false;

                checkLogin();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        _appLifecycleUseCase
                .isAppStoppedTooLong(System.currentTimeMillis())
                .subscribe(isStoppedTooLong -> _isStoppedTooLong = isStoppedTooLong);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startService(new Intent(this, CacheService.class));

        _receiver.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        _receiver.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        _appLifecycleUseCase.setAppStopped(System.currentTimeMillis()).subscribe();
    }

    @Override
    protected void onDestroy() {
        _receiver.onDestroy();

        ((LikesApplication) getApplication()).setFreshRun(false);

        super.onDestroy();
    }
}
