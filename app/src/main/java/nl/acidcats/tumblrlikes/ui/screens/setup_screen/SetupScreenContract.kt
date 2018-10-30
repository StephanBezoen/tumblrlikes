package nl.acidcats.tumblrlikes.ui.screens.setup_screen

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView

/**
 * Created on 18/10/2018.
 */
interface SetupScreenContract {

    interface Presenter : BasePresenter<View> {
        fun onViewCreated()

        fun checkCache()

        fun onBlogTextChanged(blog: String)

        fun onSetupDone(blog: String)

        fun exportPhotos(filename: String)
    }

    interface View : BaseView {
        fun setTumblrBlogText(tumblrBlog: String)

        fun enableCacheCheckButton(enable: Boolean)

        fun enableOkButton(enable: Boolean)

        fun showCacheMissToast(cacheMissCount: Int)

        fun enableExportButton(enable: Boolean)

        fun showExportCompleteToast(success: Boolean)
    }

    companion object {
        const val BLOG_EXT = ".tumblr.com"
    }
}
