package nl.acidcats.tumblrlikes.core.usecases.appsetup

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class TumblrBlogUseCaseImpl @Inject constructor(val appDataRepository: AppDataRepository) : TumblrBlogUseCase {
    override fun getTumblrBlog(): Observable<String> {
        return Observable.just(appDataRepository.getTumblrBlog())
    }

    override fun setTumblrBlog(tumblrBlog: String): Observable<Boolean> {
        appDataRepository.setTumblrBlog(tumblrBlog)

        return Observable.just(true)
    }
}