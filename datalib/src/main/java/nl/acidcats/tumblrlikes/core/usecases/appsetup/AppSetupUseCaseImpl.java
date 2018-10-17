package nl.acidcats.tumblrlikes.core.usecases.appsetup;

import android.text.TextUtils;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public class AppSetupUseCaseImpl implements AppSetupUseCase {

    private AppDataRepository _appDataRepository;

    @Inject
    public AppSetupUseCaseImpl(AppDataRepository appDataRepository) {
        _appDataRepository = appDataRepository;
    }

    @Override
    public Observable<Boolean> isSetupComplete() {
        String apiKey = _appDataRepository.getTumblrApiKey();
        String blog = _appDataRepository.getTumblrBlog();

        return Observable.just(!TextUtils.isEmpty(apiKey) && !TextUtils.isEmpty(blog));
    }
}
