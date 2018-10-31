package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.os.Bundle

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel

/**
 * Created on 19/10/2018.
 */
interface PhotoScreenContract {
    enum class HideFlow {
        INSTANT, ANIMATED
    }

    interface Presenter : BasePresenter<View>, PhotoActionListener, GestureListener {
        fun onViewCreated()

        fun onImageLoadFailed()

        fun onFilterSelected(filter: Filter)

        fun saveState(outState: Bundle)

        fun restoreState(savedInstanceState: Bundle?, args: Bundle?)

        fun onPause()

        fun onResume()
    }

    interface View : BaseView {
        fun loadPhoto(url: String?, notifyOnError: Boolean)

        fun resetPhotoScale()

        fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow)

        fun showPhotoActionDialog(viewModel: PhotoOptionsViewModel)

        fun setFilter(filter: Filter)

        fun setPhotoOptionsViewModel(viewModel: PhotoOptionsViewModel)

        fun showUI()

        fun hideUI()

        fun setPhotoVisible(visible: Boolean)
    }

    interface PhotoActionListener {
        fun onHidePhoto(id: Long)

        fun onUpdatePhotoLike(id: Long, isLiked: Boolean)

        fun onUpdatePhotoFavorite(id: Long, isFavorite: Boolean)
    }

    interface GestureListener {
        fun onSwipe()

        fun onTap()

        fun onLongPress()

        fun onDoubleTap()
    }
}
