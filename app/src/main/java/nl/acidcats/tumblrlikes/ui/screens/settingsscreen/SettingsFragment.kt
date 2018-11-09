package nl.acidcats.tumblrlikes.ui.screens.settingsscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_settings.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupFragment
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper
import nl.acidcats.tumblrlikes.util.permissions.PermissionListener
import javax.inject.Inject

/**
 * Created on 09/11/2018.
 */
class SettingsFragment : BaseFragment(), SettingsScreenContract.View {

    @Inject
    lateinit var presenter: SettingsScreenContract.Presenter
    @Inject
    lateinit var permissionHelper: PermissionHelper

    private val storagePermissionListener: PermissionListener = { permission, isGranted ->
        if (permission == PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE && isGranted) {
            exportPhotos()
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun injectFrom(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.onViewCreated()

        checkCacheButton.setOnClickListener { presenter.checkCache() }

        exportButton.setOnClickListener { checkExportPhotos() }

        refreshAllButton.setOnClickListener {
            fragmentManager?.popBackStack()

            presenter.refreshAllLikes()
        }

        closeButton.setOnClickListener { activity?.onBackPressed() }

        permissionHelper.addPermissionListener(storagePermissionListener)
    }

    private fun checkExportPhotos() {
        if (context == null || activity == null) return

        if (permissionHelper.hasPermission(context!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE)) {
            exportPhotos()
        } else {
            permissionHelper.requestPermission(activity!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE, "")
        }
    }

    private fun exportPhotos() {
        presenter.exportPhotos(SetupFragment.EXPORT_PATH)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun enableCacheCheckButton(enable: Boolean) {
        checkCacheButton.isEnabled = enable
    }

    override fun showCacheMissToast(cacheMissCount: Int) {
        Toast.makeText(context, getString(R.string.cache_miss_count, Integer.toString(cacheMissCount)), Toast.LENGTH_SHORT).show()
    }

    override fun enableExportButton(enable: Boolean) {
        exportButton.isEnabled = enable
    }

    override fun showExportCompleteToast(success: Boolean) {
        Toast.makeText(context, getString(if (success) R.string.export_success else R.string.export_error), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        permissionHelper.removePermissionListener(storagePermissionListener)

        presenter.onDestroyView()

        super.onDestroyView()
    }
}