package nl.acidcats.tumblrlikes.core.usecases.appsetup

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class AppSetupUseCaseImpl @Inject constructor(private val appDataRepository: AppDataRepository) : AppSetupUseCase {

    override fun isSetupComplete(): Observable<Boolean> {
        val apiKey = appDataRepository.getTumblrApiKey()
        val blog = appDataRepository.getTumblrBlog()

        return Observable.just(!apiKey.isEmpty() && !blog.isEmpty());
    }
}
