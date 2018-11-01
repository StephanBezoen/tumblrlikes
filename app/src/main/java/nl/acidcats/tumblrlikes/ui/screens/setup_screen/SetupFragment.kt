package nl.acidcats.tumblrlikes.ui.screens.setup_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.util.PermissionHelper
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class SetupFragment : BaseFragment(), SetupScreenContract.View {

    @Inject
    lateinit var presenter: SetupScreenContract.Presenter

    @BindView(R.id.input_tumblr_blog)
    lateinit var tumblrBlogInput: EditText
    @BindView(R.id.btn_ok)
    lateinit var okButton: TextView
    @BindView(R.id.blog_ext_txt)
    lateinit var blogExtensionText: TextView
    @BindView(R.id.txt_version)
    lateinit var versionText: TextView
    @BindView(R.id.btn_check_cache)
    lateinit var checkCacheButton: TextView
    @BindView(R.id.btn_privacy_policy)
    lateinit var privacyPolicyButton: TextView
    @BindView(R.id.btn_export_photos)
    lateinit var exportButton: TextView

    private lateinit var textWatcher: TextWatcherAdapter

    companion object {
        fun newInstance(): SetupFragment = SetupFragment()
    }

    override fun injectFrom(appComponent: AppComponent) = appComponent.inject(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_setup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.onViewCreated()

        textWatcher = object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = presenter.onBlogTextChanged(s.toString())
        }
        tumblrBlogInput.addTextChangedListener(textWatcher)

        blogExtensionText.text = SetupScreenContract.BLOG_EXT

        versionText.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        okButton.setOnClickListener {
            presenter.onSetupDone(tumblrBlogInput.getText().toString() + SetupScreenContract.Companion.BLOG_EXT)
        }

        checkCacheButton.setOnClickListener { presenter.checkCache() }

        privacyPolicyButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL)))
        }

        exportButton.setOnClickListener { checkExportPhotos() }
    }

    private fun checkExportPhotos() {
        if (PermissionHelper.getInstance().hasPermission(context, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE)) {
            presenter.exportPhotos("tumblrlikes.txt")
        } else {
            PermissionHelper.getInstance().requestPermission(activity, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE, "")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults)

        checkExportPhotos()
    }

    override fun setTumblrBlogText(tumblrBlog: String) {
        tumblrBlogInput.setText(tumblrBlog)
        tumblrBlogInput.setSelection(tumblrBlog.length)
    }

    override fun enableCacheCheckButton(enable: Boolean) {
        checkCacheButton.isEnabled = enable
    }

    override fun enableOkButton(enable: Boolean) {
        okButton.isEnabled = enable
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
        tumblrBlogInput.removeTextChangedListener(textWatcher)

        presenter.onDestroyView()

        super.onDestroyView()
    }
}