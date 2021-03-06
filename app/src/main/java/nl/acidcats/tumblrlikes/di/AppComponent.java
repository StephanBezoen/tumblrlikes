package nl.acidcats.tumblrlikes.di;

import javax.inject.Singleton;

import dagger.Component;
import nl.acidcats.tumblrlikes.data.services.CacheService;
import nl.acidcats.tumblrlikes.ui.MainActivity;
import nl.acidcats.tumblrlikes.ui.fragments.LoadLikesFragment;
import nl.acidcats.tumblrlikes.ui.fragments.LoginFragment;
import nl.acidcats.tumblrlikes.ui.fragments.PhotoFragment;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoActionDialog;
import nl.acidcats.tumblrlikes.ui.fragments.SetupFragment;

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

    void inject(PhotoActionDialog photoActionDialog);

    void inject(SetupFragment fragment);
}
