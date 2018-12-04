package nl.acidcats.tumblrlikes.ui.screens.setup_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_setup.*
import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class SetupFragment : BaseFragment(), SetupScreenContract.View {

    @Inject
    lateinit var presenter: SetupScreenContract.Presenter

    private lateinit var textWatcher: TextWatcherAdapter

    companion object {
        const val EXPORT_PATH = "tumblrlikes.txt"

        fun newInstance(): SetupFragment = SetupFragment()
    }

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

        privacyPolicyButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL)))
        }

    }

    override fun setTumblrBlogText(tumblrBlog: String) {
        tumblrBlogInput.setText(tumblrBlog)
        tumblrBlogInput.setSelection(tumblrBlog.length)
    }

    override fun enableOkButton(enable: Boolean) {
        okButton.isEnabled = enable
    }


    override fun onDestroyView() {
        tumblrBlogInput.removeTextChangedListener(textWatcher)

        presenter.onDestroyView()

        super.onDestroyView()
    }
}
