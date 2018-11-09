package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 18/10/2018.
 */
interface LoadLikesScreenContract {

    object Keys {
        const val KEY_MODE = "mode"
    }

    interface Presenter : BasePresenter<LoadLikesScreenContract.View> {
        fun onViewCreated(mode:LoadLikesMode = LoadLikesMode.SINCE_LAST)

        fun cancelLoading()

        fun skipLoading()

        fun retryLoading()

        fun showSettings()
    }

    interface View : BaseView {
        fun showLoadProgress(pageCount: Int, totalPhotoCount: Int)

        fun showErrorAlert(errorStringId: Int)

        fun showAllLikesLoaded(count: Int)

        fun showLoadingCancelled()
    }
}
