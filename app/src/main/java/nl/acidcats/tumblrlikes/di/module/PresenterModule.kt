package nl.acidcats.tumblrlikes.di.module

import dagger.Module
import dagger.Provides
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesScreenContract
import nl.acidcats.tumblrlikes.ui.screens.load_likes_screen.LoadLikesScreenPresenter
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenContract
import nl.acidcats.tumblrlikes.ui.screens.login_screen.LoginScreenPresenter
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenPresenter
import nl.acidcats.tumblrlikes.ui.screens.settingsscreen.SettingsScreenContract
import nl.acidcats.tumblrlikes.ui.screens.settingsscreen.SettingsScreenPresenter
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupScreenContract
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupScreenPresenter

/**
 * Created on 04/12/2018.
 */
@Module
class PresenterModule {
    @Provides
    fun provideSetupScreenPresenter(presenter: SetupScreenPresenter): SetupScreenContract.Presenter = presenter

    @Provides
    fun provideLoginScreenPresenter(presenter: LoginScreenPresenter): LoginScreenContract.Presenter = presenter

    @Provides
    fun provideLoadLikesScreenPresenter(presenter: LoadLikesScreenPresenter): LoadLikesScreenContract.Presenter = presenter

    @Provides
    fun providePhotoScreenPresenter(presenter: PhotoScreenPresenter): PhotoScreenContract.Presenter = presenter

    @Provides
    fun provideSettingsScreenPresenter(presenter: SettingsScreenPresenter): SettingsScreenContract.Presenter = presenter
}