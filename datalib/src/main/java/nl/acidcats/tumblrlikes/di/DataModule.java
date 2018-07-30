package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import nl.acidcats.tumblrlikes.util.security.SecurityHelperImpl;

/**
 * Created on 30/07/2018.
 */
@Module
public class DataModule {
    private Context _context;

    public DataModule(Context context) {
        _context = context;
    }

    @Provides
    @Singleton
    LikesRepo provideLikesRepos() {
        return new LikesRepoImpl(new NetLikesStore(_context));
    }

    @Provides
    @Singleton
    SecurityHelper provideSecurityHelper() {
        return new SecurityHelperImpl();
    }

    @Provides
    @Singleton
    PhotoStore providePhotoStore() {
        return new PhotoStoreImpl(_context);
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

}
