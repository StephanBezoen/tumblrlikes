package com.mediamonks.mylikes.di;

import com.mediamonks.mylikes.LikesApplication;
import com.mediamonks.mylikes.data.repo.like.LikesRepos;
import com.mediamonks.mylikes.data.repo.like.LikesReposImpl;
import com.mediamonks.mylikes.data.repo.like.store.NetLikesStore;
import com.mediamonks.mylikes.data.repo.photo.PhotoRepo;
import com.mediamonks.mylikes.data.repo.photo.PhotoRepoImpl;
import com.mediamonks.mylikes.data.repo.photo.store.PhotoStoreImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    LikesRepos provideLikesRepos() {
        return new LikesReposImpl(new NetLikesStore(_application));
    }

    @Provides
    @Singleton
    PhotoRepo providePhotoRepo() {
        return new PhotoRepoImpl(new PhotoStoreImpl(_application));
    }
}
