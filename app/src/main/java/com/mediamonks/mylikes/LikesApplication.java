package com.mediamonks.mylikes;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.mediamonks.mylikes.di.DaggerMyComponent;
import com.mediamonks.mylikes.di.MyComponent;
import com.mediamonks.mylikes.di.MyModule;
import com.pixplicity.easyprefs.library.Prefs;

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
