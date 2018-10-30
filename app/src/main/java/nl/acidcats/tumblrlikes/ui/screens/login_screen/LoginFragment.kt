package nl.acidcats.tumblrlikes.ui.screens.login_screen

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class LoginFragment : BaseFragment(), LoginScreenContract.View {

    @Inject
    lateinit var presenter: LoginScreenContract.Presenter

    @BindView(R.id.input_password)
    lateinit var passwordInput: EditText
    @BindView(R.id.tv_pincode_header)
    lateinit var header: TextView
    @BindView(R.id.btn_skip)
    lateinit var skipButton: View
    @BindView(R.id.tv_pincode_no_match)
    lateinit var pincodeNoMatchText: TextView

    private lateinit var textWatcher: TextWatcherAdapter

    companion object {
        fun newInstance(mode: LoginScreenContract.Mode): LoginFragment {
            val args = Bundle()
            args.putString(LoginScreenContract.KEY_MODE, mode.name)

            val fragment = LoginFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun injectFrom(appComponent: AppComponent) = appComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.restoreState(savedInstanceState, arguments)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presenter.saveState(outState)

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.onViewCreated()

        passwordInput.filters = arrayOf(InputFilter.LengthFilter(LoginScreenContract.PINCODE_LENGTH))

        textWatcher = object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pincode = s?.toString() ?: ""
                presenter.onPincodeInputChanged(pincode)
            }
        }
        passwordInput.addTextChangedListener(textWatcher)

        skipButton.setOnClickListener { presenter.skipLogin() }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun setPincodeDoesntMatchViewVisible(isVisible: Boolean) {
        pincodeNoMatchText.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setSkipButtonVisible(isVisible: Boolean) {
        skipButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setHeaderTextId(headerTextId: Int) {
        header.text = getString(headerTextId)
    }

    override fun clearPasswordInput() {
        passwordInput.setText("")
    }
}