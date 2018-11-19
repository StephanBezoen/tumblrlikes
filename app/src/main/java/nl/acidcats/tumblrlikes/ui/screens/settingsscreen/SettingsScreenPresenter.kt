package nl.acidcats.tumblrlikes.ui.screens.settingsscreen

import android.os.Environment
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.usecases.photos.ExportPhotosUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import javax.inject.Inject

/**
 * Created on 09/11/2018.
 */
class SettingsScreenPresenter @Inject constructor() : BasePresenterImpl<SettingsScreenContract.View>(), SettingsScreenContract.Presenter {

    @Inject
    lateinit var photoCacheUseCase: UpdatePhotoCacheUseCase
    @Inject
    lateinit var exportPhotosUseCase: ExportPhotosUseCase

    override fun onViewCreated() {
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

        getView()?.showToast(getView()?.getContext()?.getString(R.string.cache_miss_count, Integer.toString(cacheMissCount)))
    }

    private fun onCacheCheckError(throwable: Throwable) {
        Timber.e { "onCacheCheckError: ${throwable.message}" }

        getView()?.enableCacheCheckButton(true)

        getView()?.showToast(getView()?.getContext()?.getString(R.string.cache_check_error))
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

        showExportCompleteToast(false)
    }

    private fun showExportCompleteToast(success: Boolean) {
        getView()?.showToast(getView()?.getContext()?.getString(if (success) R.string.export_success else R.string.export_error))
    }

    private fun onExportComplete(isExported: Boolean) {
        getView()?.enableExportButton(true)

        showExportCompleteToast(isExported)
    }

    override fun refreshAllLikes() {
        getView()?.sendBroadcast(Broadcasts.REFRESH_ALL_REQUEST)
    }
}
