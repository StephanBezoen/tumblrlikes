package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.LikesApplication;

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
}
