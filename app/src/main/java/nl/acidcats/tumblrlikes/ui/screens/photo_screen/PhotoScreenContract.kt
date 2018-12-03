package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

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

    enum class RefreshType {
        AUTOMATIC, MANUAL
    }

    interface Presenter : BasePresenter<View>, PhotoActionListener, GestureListener, NavBarListener {
        fun onViewCreated()

        fun setScreenViewModel(viewModel: PhotoScreenViewModel)

        fun onFilterSelected(filter: FilterType)

        fun readArguments(args: Bundle?)

        fun onPause()

        fun onResume()

        fun saveBitmap(bitmap: Bitmap)
    }

    interface View : BaseView {
        fun loadPhoto(url: String, fallbackUrl: String)

        fun isPhotoScaled(): Boolean

        fun resetPhotoScale()

        fun scalePhotoToView()

        fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow)

        fun showPhotoActionDialog(point: PointF)

        fun setFilter(filter: FilterType)

        fun showUI()

        fun hideUI()

        fun enableRefreshButton(enabled: Boolean)

        fun checkSavePhoto()
    }

    interface PhotoActionListener {
        fun hidePhoto()

        fun togglePhotoLike()

        fun togglePhotoFavorite()

        fun savePhoto()
    }

    interface NavBarListener {
        fun onSettingsRequested()

        fun onRefreshRequested()
    }

    interface GestureListener {
        fun onSwipe()

        fun onTap(point: PointF)

        fun onLongPress()

        fun onDoubleTap()
    }
}

