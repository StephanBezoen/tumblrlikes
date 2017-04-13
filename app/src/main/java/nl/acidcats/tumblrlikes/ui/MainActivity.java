package nl.acidcats.tumblrlikes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.ui.fragments.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.fragments.LoginFragment;
import nl.acidcats.tumblrlikes.ui.fragments.PhotoFragment;
import nl.acidcats.tumblrlikes.util.BroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    LikesRepo _likesRepo;

    private BroadcastReceiver _receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LikesApplication) getApplication()).getMyComponent().inject(this);

        _receiver = new BroadcastReceiver(this);
        _receiver.addActionHandler(Broadcasts.PASSWORD_OK, this::onPasswordOk);
        _receiver.addActionHandler(Broadcasts.ALL_LIKES_LOADED, this::onAllLikesLoaded);
    }

    private void onAllLikesLoaded(String action, Intent intent) {
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
    protected void onResume() {
        super.onResume();

        _receiver.onResume();

        showFragment(LoginFragment.newInstance());
    }

    @Override
    protected void onPause() {
        super.onPause();

        _receiver.onPause();

        showFragment(LoginFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        _receiver.onDestroy();

        super.onDestroy();
    }
}
