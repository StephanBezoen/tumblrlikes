package nl.acidcats.tumblrlikes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.services.CacheService;
import nl.acidcats.tumblrlikes.ui.fragments.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.fragments.LoginFragment;
import nl.acidcats.tumblrlikes.ui.fragments.PhotoFragment;
import nl.acidcats.tumblrlikes.util.BroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    LikesRepo _likesRepo;

    private BroadcastReceiver _receiver;
    private boolean _isRestarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LikesApplication) getApplication()).getMyComponent().inject(this);

        _receiver = new BroadcastReceiver(this);
        _receiver.addActionHandler(Broadcasts.PASSWORD_OK, this::onPasswordOk);
        _receiver.addActionHandler(Broadcasts.ALL_LIKES_LOADED, this::onAllLikesLoaded);
        _receiver.addActionHandler(Broadcasts.DATABASE_RESET, this::onDatabaseReset);

        startService(new Intent(this, CacheService.class));

        if (savedInstanceState == null || ((LikesApplication)getApplication()).isFreshRun()) {
            showFragment(LoginFragment.newInstance());
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

        if (_isRestarted) {
            _isRestarted = false;

            showFragment(LoginFragment.newInstance());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _receiver.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        _receiver.onPause();
    }

    @Override
    protected void onDestroy() {
        _receiver.onDestroy();

        ((LikesApplication)getApplication()).setFreshRun(false);

        super.onDestroy();
    }
}
