package nl.acidcats.tumblrlikes.ui.screens.settingsscreen

import android.os.Environment
import com.github.ajalt.timberkt.Timber
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

        getView()?.showCacheMissToast(cacheMissCount)
    }

    private fun onCacheCheckError(throwable: Throwable) {
        Timber.e { "onCacheCheckError: ${throwable.message}" }

        getView()?.enableCacheCheckButton(true)
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

    override fun refreshAllLikes() {
        getView()?.sendBroadcast(Broadcasts.REFRESH_ALL_REQUEST)
    }
}
