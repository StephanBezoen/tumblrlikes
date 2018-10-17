package nl.acidcats.tumblrlikes.core.usecases.appsetup;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public class TumblrBlogUseCaseImpl implements TumblrBlogUseCase {

    private AppDataRepository _appDataRepository;

    @Inject
    public TumblrBlogUseCaseImpl(AppDataRepository appDataRepository) {
        _appDataRepository = appDataRepository;
    }

    @Override
    public Observable<Boolean> setTumblrBlog(String tumblrBlog) {
        _appDataRepository.setTumblrBlog(tumblrBlog);

        return Observable.just(true);
    }

    @Override
    public Observable<String> getTumblrBlog() {
        return Observable.just(_appDataRepository.getTumblrBlog());
    }
}
