package nl.acidcats.tumblrlikes.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.data_impl.appdata.AppDataRepositoryImpl;
import nl.acidcats.tumblrlikes.core.repositories.gateways.AppDataGateway;
import nl.acidcats.tumblrlikes.data_impl.appdata.AppDataGatewayImpl;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.data_impl.likesdata.LikesDataRepositoryImpl;
import nl.acidcats.tumblrlikes.data_impl.likesdata.NetLikesDataGateway;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.data_impl.photodata.PhotoDataRepositoryImpl;
import nl.acidcats.tumblrlikes.core.repositories.gateways.PhotoDataGateway;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway_impl.greendao.GreenDAOPhotoDataGatewayImpl;
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

}
