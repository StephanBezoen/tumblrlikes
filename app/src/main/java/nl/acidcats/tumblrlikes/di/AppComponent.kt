package nl.acidcats.tumblrlikes.di

import dagger.Component
import nl.acidcats.tumblrlikes.data.services.CacheService
import nl.acidcats.tumblrlikes.ui.MainActivity
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesFragment
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoFragment
import nl.acidcats.tumblrlikes.ui.screens.settingsscreen.SettingsFragment
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment
import javax.inject.Singleton

/**
 * Created by stephan on 28/03/2017.
 */

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)

    fun inject(fragment: LoginFragment)

    fun inject(fragment: LoadLikesFragment)

    fun inject(fragment: PhotoFragment)

    fun inject(service: CacheService)

    fun inject(fragment: SetupFragment)

    fun inject(fragment: SettingsFragment)
}
