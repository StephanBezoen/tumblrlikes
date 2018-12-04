package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Bitmap
import android.os.Bundle
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 19/10/2018.
 */
interface PhotoScreenContract {

    object Keys {
        const val REFRESH = "Refresh"
    }

    interface Presenter : BasePresenter<View> {
        fun onViewCreated()

        fun readArguments(args: Bundle?)

        fun saveBitmap(bitmap: Bitmap)

        fun refreshLikes()
    }

    interface View : BaseView {
        fun enableRefreshButton(enabled: Boolean)
    }
}
