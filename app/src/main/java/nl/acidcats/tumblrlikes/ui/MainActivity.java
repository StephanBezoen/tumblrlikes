package nl.acidcats.tumblrlikes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixplicity.easyprefs.library.Prefs;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data_impl.appdata.PrefKeys;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.data.services.CacheService;
import nl.acidcats.tumblrlikes.ui.fragments.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.fragments.LoginFragment;
import nl.acidcats.tumblrlikes.ui.fragments.PhotoFragment;
import nl.acidcats.tumblrlikes.ui.fragments.SetupFragment;
import nl.acidcats.tumblrlikes.util.BroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long MAX_STOP_TIME_MS = 1000L;

    @Inject
    LikesDataRepository _likesRepo;
    @Inject
    AppDataRepository _appRepo;
    @Inject
    FirebaseAnalytics _analytics;

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

        if (_appRepo.isSetupComplete()) {
            if (savedInstanceState == null || ((LikesApplication) getApplication()).isFreshRun()) {
                checkLogin();
            }
        } else {
            _isShowingSetup = true;

            showFragment(SetupFragment.newInstance());
        }
    }

    private void onSettingsRequest(String action, Intent intent) {
        showFragment(SetupFragment.newInstance());
    }

    private void onRefreshRequest(String action, Intent intent) {
        showFragment(LoadLikesFragment.newInstance());
    }

    private void checkLogin() {
        if (_appRepo.hasPincode()) {
            showFragment(LoginFragment.newInstance(LoginFragment.Mode.LOGIN));
        } else {
            enterApp();
        }
    }

    private void onSetupComplete(String action, Intent intent) {
        _isShowingSetup = false;

        if (_appRepo.hasPincode()) {
            enterApp();
        } else {
            showFragment(LoginFragment.newInstance(LoginFragment.Mode.NEW_PINCODE));
        }
    }

    private void onDatabaseReset(String action, Intent intent) {
        _likesRepo.reset();
    }

    private void onAllLikesLoaded(String action, Intent intent) {
        startService(new Intent(this, CacheService.class));

        showFragment(PhotoFragment.newInstance());
    }

    private void onPasswordOk(String action, Intent intent) {
        enterApp();
    }

    private void enterApp() {
        showFragment(_likesRepo.isTimeToCheck()
                ? LoadLikesFragment.newInstance()
                : PhotoFragment.newInstance());
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

        long timeDiff = System.currentTimeMillis() - Prefs.getLong(PrefKeys.KEY_APP_STOP_TIME, 0L);
        _isStoppedTooLong = (timeDiff > MAX_STOP_TIME_MS);
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

        Prefs.putLong(PrefKeys.KEY_APP_STOP_TIME, System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        _receiver.onDestroy();

        ((LikesApplication) getApplication()).setFreshRun(false);

        super.onDestroy();
    }
}
