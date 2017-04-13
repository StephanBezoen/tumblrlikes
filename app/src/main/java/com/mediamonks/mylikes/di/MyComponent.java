package com.mediamonks.mylikes.di;

import com.mediamonks.mylikes.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by stephan on 28/03/2017.
 */

@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    void inject(MainActivity activity);
}
