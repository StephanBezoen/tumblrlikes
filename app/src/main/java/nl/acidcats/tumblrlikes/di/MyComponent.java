package nl.acidcats.tumblrlikes.di;

import javax.inject.Singleton;

import dagger.Component;
import nl.acidcats.tumblrlikes.ui.MainActivity;
import nl.acidcats.tumblrlikes.ui.fragments.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.fragments.LoginFragment;
import nl.acidcats.tumblrlikes.ui.fragments.PhotoFragment;

/**
 * Created by stephan on 28/03/2017.
 */

@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    void inject(MainActivity activity);

    void inject(LoginFragment fragment);

    void inject(LoadLikesFragment fragment);

    void inject(PhotoFragment fragment);
}
