package nl.acidcats.tumblrlikes.ui.screens.setup_screen

import android.os.Environment
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.ExportPhotosUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class SetupScreenPresenter @Inject constructor() : BasePresenterImpl<SetupScreenContract.View>(), SetupScreenContract.Presenter {

    @Inject
    lateinit var photoCacheUseCase: UpdatePhotoCacheUseCase
    @Inject
    lateinit var tumblrBlogUseCase: TumblrBlogUseCase
    @Inject
    lateinit var exportPhotosUseCase: ExportPhotosUseCase

    override fun onViewCreated() {
        registerSubscription(
                tumblrBlogUseCase
                        .getTumblrBlog()
                        .subscribe {
                            var blog = it

                            @Suppress("SENSELESS_COMPARISON")
                            if (blog == null && BuildConfig.DEBUG && BuildConfig.BLOG != null && !BuildConfig.BLOG.isEmpty()) {
                                blog = BuildConfig.BLOG
                            }

                            if (blog == null) {
                                getView()?.setTumblrBlogText("")
                                getView()?.enableOkButton(false)
                            } else {
                                if (blog.endsWith(SetupScreenContract.BLOG_EXT)) {
                                    blog = blog.replace(SetupScreenContract.BLOG_EXT, "")
                                }

                                getView()?.setTumblrBlogText(blog)
                                getView()?.enableOkButton(true)
                            }
                        }
        )
    }

    override fun checkCache() {
        getView()?.enableCacheCheckButton(false)

        registerSubscription(
                photoCacheUseCase
                        .checkCachedPhotos()
                        .subscribe({ onCacheChecked(it) }, { onCacheCheckError(it) })
        )
    }

    private fun onCacheChecked(cacheMissCount: Int) {
        getView()?.enableCacheCheckButton(true)

        getView()?.showCacheMissToast(cacheMissCount)
    }

    private fun onCacheCheckError(throwable: Throwable) {
        Timber.e { "onCacheCheckError: ${throwable.message}" }

        getView()?.enableCacheCheckButton(true)
    }

    override fun onBlogTextChanged(blog: String) {
        getView()?.enableOkButton(!blog.isEmpty())
    }

    override fun onSetupDone(blog: String) {
        getView()?.enableOkButton(false)

        registerSubscription(
                tumblrBlogUseCase.setTumblrBlog(blog).subscribe { notify(Broadcasts.SETUP_COMPLETE) }
        )
    }

    override fun exportPhotos(filename: String) {
        getView()?.enableExportButton(false)

        exportPhotosUseCase
                .exportPhotos(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path, filename)
                .subscribe({ onExportComplete(it) }, { onExportError(it) })
    }

    private fun onExportError(throwable: Throwable) {
        Timber.e { "onExportError: ${throwable.message}" }

        getView()?.enableExportButton(true)
        getView()?.showExportCompleteToast(false)
    }

    private fun onExportComplete(isExported: Boolean) {
        getView()?.enableExportButton(true)
        getView()?.showExportCompleteToast(isExported)
    }
}