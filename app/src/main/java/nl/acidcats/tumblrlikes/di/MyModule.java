package nl.acidcats.tumblrlikes.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepoImpl;
import nl.acidcats.tumblrlikes.data.repo.like.store.NetLikesStore;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepoImpl;
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

    public MyModule(LikesApplication application) {
        _application = application;
    }

    @Provides
    @Singleton
    LikesRepo provideLikesRepos() {
        return new LikesRepoImpl(new NetLikesStore(_application));
    }

    @Provides
    @Singleton
    PhotoRepo providePhotoRepo() {
        return new PhotoRepoImpl(new PhotoStoreImpl(_application));
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
}