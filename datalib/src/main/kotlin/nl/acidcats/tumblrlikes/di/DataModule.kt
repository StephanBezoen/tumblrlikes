package nl.acidcats.tumblrlikes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import nl.acidcats.tumblrlikes.core.usecases.appsetup.AppSetupUseCase
import nl.acidcats.tumblrlikes.core.usecases.appsetup.AppSetupUseCaseImpl
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCase
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCaseImpl
import nl.acidcats.tumblrlikes.core.usecases.checktime.CheckTimeUseCase
import nl.acidcats.tumblrlikes.core.usecases.checktime.CheckTimeUseCaseImpl
import nl.acidcats.tumblrlikes.core.usecases.lifecycle.AppLifecycleUseCase
import nl.acidcats.tumblrlikes.core.usecases.lifecycle.AppLifecycleUseCaseImpl
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCase
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCaseImpl
import nl.acidcats.tumblrlikes.core.usecases.photos.*
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCaseImpl
import nl.acidcats.tumblrlikes.data_impl.appdata.AppDataRepositoryImpl
import nl.acidcats.tumblrlikes.data_impl.likesdata.LikesDataRepositoryImpl
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.NetLikesDataGateway
import nl.acidcats.tumblrlikes.data_impl.photodata.PhotoDataRepositoryImpl
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.GreenDAOPhotoDataGatewayImpl
import nl.acidcats.tumblrlikes.util.security.SecurityHelper
import nl.acidcats.tumblrlikes.util.security.SecurityHelperImpl
import javax.inject.Singleton

/**
 * Created on 30/07/2018.
 */
@Module
class DataModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideLikesDataRepository(): LikesDataRepository {
        return LikesDataRepositoryImpl(NetLikesDataGateway(context))
    }

    @Provides
    @Singleton
    internal fun provideSecurityHelper(): SecurityHelper {
        return SecurityHelperImpl()
    }

    @Provides
    @Singleton
    internal fun providePhotoDataGateway(): PhotoDataGateway {
        return GreenDAOPhotoDataGatewayImpl(context)
    }

    @Provides
    @Singleton
    internal fun providePhotoDataRepository(impl: PhotoDataRepositoryImpl): PhotoDataRepository {
        return impl
    }

    @Provides
    @Singleton
    internal fun provideAppDataRepository(impl: AppDataRepositoryImpl): AppDataRepository {
        return impl
    }

    @Provides
    internal fun provideLikesPageUseCase(impl: GetLikesPageUseCaseImpl): GetLikesPageUseCase {
        return impl
    }

    @Provides
    internal fun provideCheckPincodeUseCase(impl: PincodeUseCaseImpl): PincodeUseCase {
        return impl
    }

    @Provides
    internal fun providePhotoCacheUseCase(impl: UpdatePhotoCacheUseCaseImpl): UpdatePhotoCacheUseCase {
        return impl
    }

    @Provides
    internal fun provideUpdatePhotoProperty(impl: UpdatePhotoPropertyUseCaseImpl): UpdatePhotoPropertyUseCase {
        return impl
    }

    @Provides
    internal fun provideCheckTimeUseCase(impl: CheckTimeUseCaseImpl): CheckTimeUseCase {
        return impl
    }

    @Provides
    internal fun provideAppLifecycleUseCase(impl: AppLifecycleUseCaseImpl): AppLifecycleUseCase {
        return impl
    }

    @Provides
    internal fun providesAppSetupUseCase(impl: AppSetupUseCaseImpl): AppSetupUseCase {
        return impl
    }

    @Provides
    internal fun providesTumblrBlogUseCase(impl: TumblrBlogUseCaseImpl): TumblrBlogUseCase {
        return impl
    }

    @Provides
    internal fun providePhotoViewUseCase(impl: PhotoViewUseCaseImpl): PhotoViewUseCase {
        return impl
    }

    @Provides
    internal fun providePhotoFilterUseCase(impl: PhotoFilterUseCaseImpl): PhotoFilterUseCase {
        return impl
    }

    @Provides
    internal fun provideGetFilteredPhotoUseCase(impl: GetFilteredPhotoUseCaseImpl): GetFilteredPhotoUseCase {
        return impl
    }

    @Provides
    internal fun provideExportPhotosUseCase(impl: ExportPhotosUseCaseImpl): ExportPhotosUseCase {
        return impl
    }
}
