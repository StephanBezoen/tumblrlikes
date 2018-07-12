package nl.acidcats.tumblrlikes.testmoshi.di;

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
import nl.acidcats.tumblrlikes.testmoshi.TestMoshiApplication;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import nl.acidcats.tumblrlikes.util.security.SecurityHelperImpl;

/**
 * Created on 10/07/2018.
 */
@Module
public class MyModule {
    private TestMoshiApplication _application;

    public MyModule(TestMoshiApplication application) {
        _application = application;
    }

    @Provides
    @Singleton
    LikesRepo provideLikesRepos() {
        return new LikesRepoImpl(new NetLikesStore(_application));
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
    @Singleton
    SecurityHelper provideSecurityHelper() {
        return new SecurityHelperImpl();
    }
}
