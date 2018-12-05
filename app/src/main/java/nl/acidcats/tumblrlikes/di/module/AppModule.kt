package nl.acidcats.tumblrlikes.di.module

import dagger.Module
import dagger.Provides
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelperImpl
import javax.inject.Singleton

/**
 * Created on 29/10/2018.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun providePermissionHelper(): PermissionHelper {
        return PermissionHelperImpl()
    }

}