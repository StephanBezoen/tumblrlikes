package nl.acidcats.tumblrlikes.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nl.acidcats.tumblrlikes.di.viewmodel.ViewModelFactory
import nl.acidcats.tumblrlikes.di.viewmodel.ViewModelKey
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenViewModel

/**
 * Created on 04/12/2018.
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotoScreenViewModel::class)
    abstract fun bindPhotoScreenViewModel(viewModel: PhotoScreenViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory:ViewModelFactory): ViewModelProvider.Factory
}