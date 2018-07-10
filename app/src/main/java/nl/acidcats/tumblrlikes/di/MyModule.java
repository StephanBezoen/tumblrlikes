package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepo;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepoImpl;
import nl.acidcats.tumblrlikes.data.repo.app.store.AppStore;
import nl.acidcats.tumblrlikes.data.repo.app.store.AppStoreImpl;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepoImpl;
import nl.acidcats.tumblrlikes.data.repo.like.store.NetLikesStore;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepoImpl;
import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStore;
import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStoreImpl;
import nl.acidcats.tumblrlikes.data.usecase.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.data.usecase.GetLikesPageUseCaseImpl;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import nl.acidcats.tumblrlikes.util.security.SecurityHelperImpl;

/**
 * Created by stephan on 28/03/2017.
 */

@Module
public class MyModule {
    private LikesApplication _application;
    private FirebaseAnalytics _analytics;

    public MyModule(LikesApplication application, FirebaseAnalytics analytics) {
        _application = application;
        _analytics = analytics;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return _application;
    }

    @Provides
    @Singleton
    LikesRepo provideLikesRepos() {
        return new LikesRepoImpl(new NetLikesStore(_application));
    }

    @Provides
    @Singleton
    SecurityHelper provideSecurityHelper() {
        return new SecurityHelperImpl();
    }

    @Provides
    GetLikesPageUseCase provideLikesPageUseCase(GetLikesPageUseCaseImpl impl) {
        return impl;
    }

    @Provides
    @Singleton
    PhotoStore providePhotoStore() {
        return new PhotoStoreImpl(_application);
    }

    @Provides
    @Singleton
    PhotoRepo providePhotoRepo(PhotoRepoImpl impl) {
        return impl;
    }

    @Provides
    @Singleton
    AppStore provideAppStore() {
        return new AppStoreImpl();
    }

    @Provides
    @Singleton
    AppRepo provideAppRepo(AppRepoImpl impl) {
        return impl;
    }

    @Provides
    FirebaseAnalytics provideAnalytics() {
        return _analytics;
    }
}
