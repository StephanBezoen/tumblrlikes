package nl.acidcats.tumblrlikes.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nl.acidcats.tumblrlikes.ui.MainActivity

/**
 * Created on 04/12/2018.
 */
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}