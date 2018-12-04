package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.SaveScreenshotUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import java.util.*
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoScreenPresenter @Inject constructor() : BasePresenterImpl<PhotoScreenContract.View>(), PhotoScreenContract.Presenter {

    enum class RefreshType {
        AUTOMATIC, MANUAL
    }

    @Inject
    lateinit var getLikesUseCase: GetLikesUseCase
    @Inject
    lateinit var saveScreenshotUseCase: SaveScreenshotUseCase

    private val loadingInterruptor: MutableList<Boolean> = ArrayList()
    private var shouldRefresh = false

    override fun onViewCreated() {
        if (shouldRefresh) {
            shouldRefresh = false

            getView()?.clearArgument(PhotoScreenContract.Keys.REFRESH)

            refreshLikes(RefreshType.AUTOMATIC)
        }
    }

    override fun readArguments(args: Bundle?) {
        shouldRefresh = args?.getBoolean(PhotoScreenContract.Keys.REFRESH) ?: false
    }

    override fun refreshLikes() {
        refreshLikes(RefreshType.MANUAL)
    }

    private fun refreshLikes(refreshType: RefreshType) {
        getView()?.enableRefreshButton(false)

        registerSubscription(
                getLikesUseCase
                        .loadAllLikes(LoadLikesMode.SINCE_LAST, loadingInterruptor, Date().time)
                        .subscribe({ handleLikesLoaded(it, refreshType) }, { handleLoadPageError(it) })
        )
    }

    private fun handleLoadPageError(throwable: Throwable) {
        getView()?.enableRefreshButton(true)

        showRefreshCompleteToast(false)
    }

    private fun showRefreshCompleteToast(success: Boolean, photoCount: Int = 0) {
        val message = if (success) {
            if (photoCount == 0) {
                getView()?.getContext()?.getString(R.string.refresh_success_no_new_photos)
            } else {
                getView()?.getContext()?.getString(R.string.refresh_success, photoCount.toString())
            }
        } else {
            getView()?.getContext()?.getString(R.string.refresh_error)
        }

        getView()?.showToast(message)
    }

    private fun handleLikesLoaded(photoCount: Int, refreshType: RefreshType) {
        getView()?.enableRefreshButton(true)

        when (refreshType) {
            RefreshType.MANUAL -> showRefreshCompleteToast(true, photoCount)
            RefreshType.AUTOMATIC -> if (photoCount > 0) {
                showRefreshCompleteToast(true, photoCount)
            }
        }

        if (photoCount > 0) {
            getView()?.sendBroadcast(Broadcasts.CACHE_SERVICE_REQUEST)
        }
    }

    override fun saveBitmap(bitmap: Bitmap) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/tumblrlikes"
        val filename = Date().time.toString() + ".jpg"

        registerSubscription(
                saveScreenshotUseCase
                        .saveScreenshot(bitmap, path, filename)
                        .subscribe({ isSaved ->
                            if (isSaved) {
                                getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_saved, filename))
                            } else {
                                getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_save_error))
                            }
                        }, {
                            getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_save_error))
                        })
        )
    }
}