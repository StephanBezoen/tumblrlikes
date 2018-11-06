package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 18/10/2018.
 */
interface LoadLikesScreenContract {

    interface Presenter : BasePresenter<LoadLikesScreenContract.View> {
        fun onViewCreated()

        fun cancelLoading()

        fun skipLoading()

        fun retryLoading()

        fun showSettings()
    }

    interface View : BaseView {
        fun showLoadProgress(pageCount: Int, totalPhotoCount: Int)

        fun showErrorAlert(errorStringId: Int)

        fun showAllLikesLoaded(count: Long)

        fun showLoadingCancelled()
    }
}
