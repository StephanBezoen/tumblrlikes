package nl.acidcats.tumblrlikes;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.pixplicity.easyprefs.library.Prefs;

import nl.acidcats.tumblrlikes.di.DaggerMyComponent;
import nl.acidcats.tumblrlikes.di.MyComponent;
import nl.acidcats.tumblrlikes.di.MyModule;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesApplication extends Application {
    private static final String TAG = LikesApplication.class.getSimpleName();

    private MyComponent _myComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Prefs.initPrefs(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        _myComponent = DaggerMyComponent.builder()
                .myModule(new MyModule(this))
                .build();

    }

    public MyComponent getMyComponent() {
        return _myComponent;
    }
}
