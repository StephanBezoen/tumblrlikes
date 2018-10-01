package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.data_impl.appdata.gateway.AppDataGateway;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway;
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCaseImpl;
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase;
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCaseImpl;
import nl.acidcats.tumblrlikes.data_impl.appdata.gateway.AppDataGatewayImpl;
import nl.acidcats.tumblrlikes.data_impl.appdata.AppDataRepositoryImpl;
import nl.acidcats.tumblrlikes.data_impl.likesdata.LikesDataRepositoryImpl;
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.NetLikesDataGateway;
import nl.acidcats.tumblrlikes.data_impl.photodata.PhotoDataRepositoryImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.GreenDAOPhotoDataGatewayImpl;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import nl.acidcats.tumblrlikes.util.security.SecurityHelperImpl;

/**
 * Created on 30/07/2018.
 */
@Module
public class DataModule {
    private Context _context;

    public DataModule(Context context) {
        _context = context;
    }

    @Provides
    @Singleton
    LikesDataRepository provideLikesDataRepository() {
        return new LikesDataRepositoryImpl(new NetLikesDataGateway(_context));
    }

    @Provides
    @Singleton
    SecurityHelper provideSecurityHelper() {
        return new SecurityHelperImpl();
    }

    @Provides
    @Singleton
    PhotoDataGateway providePhotoDataGateway() {
        return new GreenDAOPhotoDataGatewayImpl(_context);
    }

    @Provides
    @Singleton
    PhotoDataRepository providePhotoDataRepository(PhotoDataRepositoryImpl impl) {
        return impl;
    }

    @Provides
    @Singleton
    AppDataGateway provideAppDataGateway() {
        return new AppDataGatewayImpl();
    }

    @Provides
    @Singleton
    AppDataRepository provideAppDataRepository(AppDataRepositoryImpl impl) {
        return impl;
    }

    @Provides
    GetLikesPageUseCase provideLikesPageUseCase(GetLikesPageUseCaseImpl impl) {
        return impl;
    }

    @Provides
    PincodeUseCase provideCheckPincodeUseCase(PincodeUseCaseImpl impl) {
        return impl;
    }
}
