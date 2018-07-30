package nl.acidcats.tumblrlikes;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixplicity.easyprefs.library.Prefs;

import io.fabric.sdk.android.Fabric;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.di.AppModule;
import nl.acidcats.tumblrlikes.di.DaggerAppComponent;
import nl.acidcats.tumblrlikes.di.DataModule;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesApplication extends Application {
    private static final String TAG = LikesApplication.class.getSimpleName();

    private AppComponent _appComponent;
    private boolean _isFreshRun = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Prefs.initPrefs(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

        _appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this, analytics))
                .dataModule(new DataModule(this))
                .build();

    }

    public AppComponent getAppComponent() {
        return _appComponent;
    }

    public boolean isFreshRun() {
        return _isFreshRun;
    }

    public void setFreshRun(boolean freshRun) {
        _isFreshRun = freshRun;
    }
}
