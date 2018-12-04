package nl.acidcats.tumblrlikes.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesFragment
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoFragment
import nl.acidcats.tumblrlikes.ui.screens.settingsscreen.SettingsFragment
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment

/**
 * Created on 04/12/2018.
 */
@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeLoadLikesFragment(): LoadLikesFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributePhotoFragment(): PhotoFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeSetupFragment(): SetupFragment
}