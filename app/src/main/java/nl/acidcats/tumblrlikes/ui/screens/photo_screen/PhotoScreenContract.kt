package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.PointF
import android.os.Bundle
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel

/**
 * Created on 19/10/2018.
 */
interface PhotoScreenContract {
    enum class HideFlow {
        INSTANT, ANIMATED
    }

    object Keys {
        const val REFRESH = "Refresh"
    }

    interface Presenter : BasePresenter<View>, PhotoActionListener, GestureListener, NavBarListener {
        fun onViewCreated()

        fun onFilterSelected(filter: FilterType)

        fun saveState(outState: Bundle)

        fun restoreState(savedInstanceState: Bundle?, args: Bundle?)

        fun onPause()

        fun onResume()
    }

    interface View : BaseView {
        fun loadPhoto(url: String, fallbackUrl: String)

        fun isPhotoScaled(): Boolean

        fun resetPhotoScale()

        fun scalePhotoToView()

        fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow)

        fun showPhotoActionDialog(viewModel: PhotoOptionsViewModel, point:PointF)

        fun setFilter(filter: FilterType)

        fun setPhotoOptionsViewModel(viewModel: PhotoOptionsViewModel)

        fun showUI()

        fun hideUI()

        fun setPhotoVisible(visible: Boolean)

        fun enableRefreshButton(enabled: Boolean)

        fun showRefreshCompleteToast(success: Boolean, photoCount: Int = 0)
    }

    interface PhotoActionListener {
        fun onHidePhoto(id: Long)

        fun onUpdatePhotoLike(id: Long, isLiked: Boolean)

        fun onUpdatePhotoFavorite(id: Long, isFavorite: Boolean)
    }

    interface NavBarListener {
        fun onSettingsRequested()

        fun onRefreshRequested()
    }

    interface GestureListener {
        fun onSwipe()

        fun onTap(point:PointF)

        fun onLongPress()

        fun onDoubleTap()
    }
}

