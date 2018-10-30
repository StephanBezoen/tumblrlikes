package nl.acidcats.tumblrlikes.ui.screens.login_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoginFragment extends BaseFragment implements LoginScreenContract.View {
    private static final String TAG = LoginFragment.class.getSimpleName();

    @Inject
    LoginScreenContract.Presenter _presenter;

    @BindView(R.id.input_password)
    EditText _passwordInput;
    @BindView(R.id.tv_pincode_header)
    TextView _header;
    @BindView(R.id.btn_skip)
    View _skipButton;
    @BindView(R.id.tv_pincode_no_match)
    TextView _pincodeNoMatchText;

    private TextWatcherAdapter _textWatcher;

    public static LoginFragment newInstance(LoginScreenContract.Mode mode) {
        Bundle args = new Bundle();
        args.putString(LoginScreenContract.Companion.KEY_MODE, mode.name());

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _presenter.restoreState(savedInstanceState, getArguments());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        _presenter.saveState(outState);

        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _presenter.setView(this);
        _presenter.onViewCreated();

        _passwordInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(LoginScreenContract.Companion.PINCODE_LENGTH)});

        _textWatcher = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _presenter.onPincodeInputChanged(s.toString());
            }
        };
        _passwordInput.addTextChangedListener(_textWatcher);

        _skipButton.setOnClickListener(v -> _presenter.skipLogin());
    }

    @Override
    public void setHeaderTextId(@StringRes int headerTextId) {
        _header.setText(headerTextId);
    }

    @Override
    public void setPincodeDoesntMatchViewVisible(boolean isVisible) {
        _pincodeNoMatchText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSkipButtonVisible(boolean isVisible) {
        _skipButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearPasswordInput() {
        _passwordInput.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        _passwordInput.removeTextChangedListener(_textWatcher);
        _skipButton.setOnClickListener(null);

        _presenter.onDestroyView();

        super.onDestroyView();
    }
}
