package nl.acidcats.tumblrlikes.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nl.acidcats.tumblrlikes.data.services.CacheService

/**
 * Created on 04/12/2018.
 */
@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeService(): CacheService
}