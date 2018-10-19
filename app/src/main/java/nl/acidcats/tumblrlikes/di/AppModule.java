package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesScreenContract;
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesScreenPresenter;
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenContract;
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenPresenter;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract;
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenPresenter;
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupScreenContract;
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupScreenPresenter;

/**
 * Created by stephan on 28/03/2017.
 */

@Module
public class AppModule {
    private LikesApplication _application;
    private FirebaseAnalytics _analytics;

    public AppModule(LikesApplication application, FirebaseAnalytics analytics) {
        _application = application;
        _analytics = analytics;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return _application;
    }

    @Provides
    FirebaseAnalytics provideAnalytics() {
        return _analytics;
    }

    @Provides
    SetupScreenContract.Presenter provideSetupScreenPresenter(SetupScreenPresenter presenter) {
        return presenter;
    }

    @Provides
    LoginScreenContract.Presenter provideLoginScreenPresenter(LoginScreenPresenter presenter) {
        return presenter;
    }

    @Provides
    LoadLikesScreenContract.Presenter provideLoadLikesScreenPresenter(LoadLikesScreenPresenter presenter) {
        return presenter;
    }

    @Provides
    PhotoScreenContract.Presenter providePhotoScreenPresenter(PhotoScreenPresenter presenter) {
        return presenter;
    }
}
