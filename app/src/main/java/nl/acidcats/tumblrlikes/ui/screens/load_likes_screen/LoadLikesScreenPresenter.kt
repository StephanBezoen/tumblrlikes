package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen

import android.os.Handler
import androidx.annotation.StringRes
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase
import nl.acidcats.tumblrlikes.data_impl.likesdata.LoadLikesException
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import rx.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created on 30/10/2018.
 */
class LoadLikesScreenPresenter @Inject constructor() : BasePresenterImpl<LoadLikesScreenContract.View>(), LoadLikesScreenContract.Presenter {

    @Inject
    lateinit var getLikesUseCase: GetLikesUseCase
    @Inject
    lateinit var updatePhotoCacheUseCase: UpdatePhotoCacheUseCase

    private var pageCount: Int = 0
    private var isLoadingCancelled = false
    private val loadingInterruptor: MutableList<Boolean> = ArrayList()
    private val pageProgress: BehaviorSubject<Int> = BehaviorSubject.create()
    private lateinit var mode: LoadLikesMode

    override fun onViewCreated(mode: LoadLikesMode) {
        this.mode = mode

        registerSubscription(
                updatePhotoCacheUseCase
                        .removeCachedHiddenPhotos()
                        .subscribe({
                            startLoadingLikes(mode)
                        }, {
                            Timber.e { "removeCachedHiddenPhotos: ${it.message}" }

                            startLoadingLikes(mode)
                        })
        )

        pageProgress.subscribe { photoCount ->
            pageCount++

            getView()?.showLoadProgress(pageCount, photoCount!!)
        }
    }

    private fun startLoadingLikes(mode: LoadLikesMode) {
        loadingInterruptor.clear()

        pageCount = 0

        registerSubscription(
                getLikesUseCase
                        .loadAllLikes(mode, loadingInterruptor, Date().time, pageProgress)
                        .subscribe({ handleLikesLoaded(it) }, { handleLoadPageError(it) })
        )
    }

    private fun handleLikesLoaded(totalPhotoCount: Int) {
        if (isLoadingCancelled) {
            notifyLoadingComplete()
        } else {
            getView()?.showAllLikesLoaded(totalPhotoCount)

            Handler().postDelayed({ notifyLoadingComplete() }, 500)
        }
    }

    private fun handleLoadPageError(throwable: Throwable) {
        @StringRes var errorStringId: Int = R.string.error_load

        if (throwable is LoadLikesException) {
            when (throwable.code) {
                403 -> errorStringId = R.string.error_403
                404 -> errorStringId = R.string.error_404
                in 300..500 -> errorStringId = R.string.error_300_400
                in 500..600 -> errorStringId = R.string.error_500
            }
        }

        getView()?.showErrorAlert(errorStringId)
    }

    override fun cancelLoading() {
        Timber.d { "cancelLoading: " }

        isLoadingCancelled = true

        loadingInterruptor += true

        getView()?.showLoadingCancelled()
    }

    override fun skipLoading() = notifyLoadingComplete()

    override fun retryLoading() = startLoadingLikes(mode)

    override fun showSettings() = notify(Broadcasts.SETTINGS_REQUEST)

    private fun notifyLoadingComplete() = notify(Broadcasts.ALL_LIKES_LOADED)
}
