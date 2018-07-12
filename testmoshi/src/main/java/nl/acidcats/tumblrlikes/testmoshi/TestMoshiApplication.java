package nl.acidcats.tumblrlikes.testmoshi;

import android.app.Application;

import com.pixplicity.easyprefs.library.Prefs;

import nl.acidcats.tumblrlikes.testmoshi.di.DaggerMyComponent;
import nl.acidcats.tumblrlikes.testmoshi.di.MyComponent;
import nl.acidcats.tumblrlikes.testmoshi.di.MyModule;

/**
 * Created on 10/07/2018.
 */
public class TestMoshiApplication extends Application {
    private MyComponent _myComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Prefs.initPrefs(this);

        _myComponent = DaggerMyComponent.builder()
                .myModule(new MyModule(this))
                .build();
    }

    public MyComponent getMyComponent() {
        return _myComponent;
    }
}
