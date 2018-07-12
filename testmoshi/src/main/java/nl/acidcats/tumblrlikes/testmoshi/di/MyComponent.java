package nl.acidcats.tumblrlikes.testmoshi.di;

import javax.inject.Singleton;

import dagger.Component;
import nl.acidcats.tumblrlikes.testmoshi.TestActivity;

/**
 * Created on 10/07/2018.
 */
@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    void inject(TestActivity activity);
}
