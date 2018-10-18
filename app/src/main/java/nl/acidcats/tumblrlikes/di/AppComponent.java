package nl.acidcats.tumblrlikes.di;

import javax.inject.Singleton;

import dagger.Component;
import nl.acidcats.tumblrlikes.data.services.CacheService;
import nl.acidcats.tumblrlikes.ui.MainActivity;
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginFragment;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoFragment;
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment;

/**
 * Created by stephan on 28/03/2017.
 */

@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(LoginFragment fragment);

    void inject(LoadLikesFragment fragment);

    void inject(PhotoFragment fragment);

    void inject(CacheService service);

    void inject(SetupFragment fragment);
}
