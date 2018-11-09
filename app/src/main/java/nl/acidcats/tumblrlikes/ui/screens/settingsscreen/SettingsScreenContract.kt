package nl.acidcats.tumblrlikes.ui.screens.settingsscreen

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 09/11/2018.
 */
interface SettingsScreenContract {

    interface Presenter : BasePresenter<View> {
        fun onViewCreated()

        fun checkCache()

        fun exportPhotos(filename: String)

        fun refreshAllLikes()
    }

    interface View : BaseView {
        fun enableCacheCheckButton(enable: Boolean)

        fun showCacheMissToast(cacheMissCount: Int)

        fun enableExportButton(enable: Boolean)

        fun showExportCompleteToast(success: Boolean)
    }
}