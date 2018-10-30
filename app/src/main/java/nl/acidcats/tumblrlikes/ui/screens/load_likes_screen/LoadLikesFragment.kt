package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.R.*
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class LoadLikesFragment : BaseFragment(), LoadLikesScreenContract.View {

    @Inject
    lateinit var presenter: LoadLikesScreenContract.Presenter

    @BindView(R.id.tv_image_count)
    lateinit var imageCountText: TextView
    @BindView(R.id.tv_loading)
    lateinit var loadingText: TextView
    @BindView(R.id.spinner)
    lateinit var spinner: ProgressBar
    @BindView(R.id.btn_cancel)
    lateinit var cancelButton: Button

    companion object {
        fun newInstance() = LoadLikesFragment()
    }

    override fun injectFrom(appComponent: AppComponent) = appComponent.inject(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loadlikes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.onViewCreated()

        if (context != null) {
            spinner.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context!!, color.colorPrimaryDark), PorterDuff.Mode.SRC_IN)
        }

        cancelButton.setOnClickListener { presenter.cancelLoading() }
    }

    override fun showLoadProgress(pageCount: Int, totalPhotoCount: Long) {
        imageCountText.text = getString(string.image_page_count, pageCount, totalPhotoCount)
    }

    override fun showErrorAlert(errorStringId: Int) {
        if (context != null) {
            AlertDialog.Builder(context!!)
                    .setTitle(string.error_title)
                    .setMessage(errorStringId)
                    .setPositiveButton(string.btn_retry) { _, _ -> presenter.retryLoading() }
                    .setNeutralButton(string.btn_settings) { _, _ -> presenter.showSettings() }
                    .setNegativeButton(string.btn_cancel) { _, _ -> presenter.skipLoading() }
                    .create()
                    .show();
        }
    }

    override fun showAllLikesLoaded(count: Long) {
        imageCountText.text = getString(string.total_image_count, count)
        loadingText.text = getString(string.all_loaded)
    }

    override fun showLoadingCancelled() {
        imageCountText.text = getString(string.loading_cancelled)

        cancelButton.isEnabled = false
    }
}