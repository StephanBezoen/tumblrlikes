package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen

import android.os.Handler
import android.support.annotation.StringRes
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.usecases.checktime.CheckTimeUseCase
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase
import nl.acidcats.tumblrlikes.data_impl.likesdata.LoadLikesException
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import java.util.*
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class LoadLikesScreenPresenter @Inject constructor() : BasePresenterImpl<LoadLikesScreenContract.View>(), LoadLikesScreenContract.Presenter {

    @Inject
    lateinit var likesPageUseCase: GetLikesPageUseCase
    @Inject
    lateinit var photoCacheUseCase: UpdatePhotoCacheUseCase
    @Inject
    lateinit var checkTimeUseCase: CheckTimeUseCase

    private var pageCount: Int = 0
    private var isLoadingCancelled = false


    override fun onViewCreated() {
        registerSubscription(
                photoCacheUseCase
                        .removeCachedHiddenPhotos()
                        .subscribe({
                            startLoadingLikes()
                        }, {
                            Timber.e { "removeCachedHiddenPhotos: ${it.message}" }

                            startLoadingLikes()
                        })
        )
    }

    private fun startLoadingLikes() {
        loadLikesPage(LoadLikesMode.SINCE_LAST)
    }

    private fun loadLikesPage(mode: LoadLikesMode) {
        registerSubscription(
                likesPageUseCase
                        .loadLikesPage(mode)
                        .subscribe({ handleLikesLoaded(it) }, { handleLoadPageError(it) })
        )
    }

    private fun handleLikesLoaded(totalPhotoCount: Long) {
        if (isLoadingCancelled) {
            notifyLoadingComplete()

            return
        }

        pageCount++

        registerSubscription(
                likesPageUseCase
                        .checkLoadLikesComplete()
                        .subscribe({
                            if (it) {
                                onLoadComplete(totalPhotoCount)
                            } else {
                                getView()?.showLoadProgress(pageCount, totalPhotoCount)

                                loadLikesPage(LoadLikesMode.NEXT_PAGE)
                            }
                        }, {
                            Timber.e { "checkLoadLikesComplete: ${it.message}" }
                        })
        )
    }

    private fun onLoadComplete(totalPhotoCount: Long) {
        getView()?.showAllLikesLoaded(totalPhotoCount)

        registerSubscription(
                checkTimeUseCase
                        .setLastCheckTime(Date().time)
                        .subscribe { Handler().postDelayed({ notifyLoadingComplete() }, 500) }
        )
    }

    private fun handleLoadPageError(throwable: Throwable) {
        Timber.e { "handleLoadPageError: ${throwable.message}" }

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
        isLoadingCancelled = true

        getView()?.showLoadingCancelled()
    }

    override fun skipLoading() = notifyLoadingComplete()

    override fun retryLoading() = startLoadingLikes()

    override fun showSettings() = notify(Broadcasts.SETTINGS_REQUEST)

    private fun notifyLoadingComplete() = notify(Broadcasts.ALL_LIKES_LOADED)
}
