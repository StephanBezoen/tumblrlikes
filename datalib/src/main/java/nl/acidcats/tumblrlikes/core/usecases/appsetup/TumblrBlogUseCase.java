package nl.acidcats.tumblrlikes.core.usecases.appsetup;

import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public interface TumblrBlogUseCase {
    Observable<Boolean> setTumblrBlog(String tumblrBlog);

    Observable<String> getTumblrBlog();
}
