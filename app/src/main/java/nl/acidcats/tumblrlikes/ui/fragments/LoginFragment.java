package nl.acidcats.tumblrlikes.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import nl.acidcats.tumblrlikes.core.usecases.pincode.PincodeUseCase;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoginFragment extends BaseFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private static final String KEY_MODE = "key_mode";
    private static final int PINCODE_LENGTH = 4;

    public enum Mode {
        NEW_PINCODE, REPEAT_PINCODE, LOGIN
    }

    @Inject
    SecurityHelper _securityHelper;
    @Inject
    PincodeUseCase _pincodeUseCase;

    @BindView(R.id.input_password)
    EditText _passwordInput;
    @BindView(R.id.tv_pincode_header)
    TextView _header;
    @BindView(R.id.btn_skip)
    View _skipButton;
    @BindView(R.id.tv_pincode_no_match)
    TextView _pincodeNoMatchText;

    private TextWatcherAdapter _textWatcher;
    private Mode _mode;
    private String _tempPincodeHash;

    public static LoginFragment newInstance(Mode mode) {
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mode.ordinal());

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

        Bundle args = getArguments();

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MODE)) {
            _mode = Mode.values()[savedInstanceState.getInt(KEY_MODE)];
        } else if (args != null && args.containsKey(KEY_MODE)) {
            _mode = Mode.values()[args.getInt(KEY_MODE)];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputFilter filter = new InputFilter.LengthFilter(PINCODE_LENGTH);
        _passwordInput.setFilters(new InputFilter[]{filter});

        _textWatcher = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LoginFragment.this.onTextChanged(s.toString());
            }
        };
        _passwordInput.addTextChangedListener(_textWatcher);

        updateUI();
    }

    private void updateUI() {
        _pincodeNoMatchText.setVisibility(View.GONE);

        switch (_mode) {
            case LOGIN:
                _header.setText(R.string.enter_pincode);
                _skipButton.setVisibility(View.GONE);
                break;
            case NEW_PINCODE:
                _header.setText(R.string.enter_new_pincode);
                _skipButton.setOnClickListener(this::onSkipButtonClick);
                break;
            case REPEAT_PINCODE:
                _header.setText(R.string.repeat_new_pincode);
                _skipButton.setOnClickListener(this::onSkipButtonClick);
                break;
        }
    }

    private void onSkipButtonClick(View view) {
        sendBroadcast(Broadcasts.PASSWORD_OK);
    }

    private void onTextChanged(String pincode) {
        _pincodeNoMatchText.setVisibility(View.GONE);

        switch (_mode) {
            case LOGIN:
                registerSubscription(
                        _pincodeUseCase.checkPincode(pincode).subscribe(
                                isCorrect -> {
                                    if (isCorrect) {
                                        sendBroadcast(Broadcasts.PASSWORD_OK);
                                    }
                                }));
                break;
            case NEW_PINCODE:
                if (pincode.length() == PINCODE_LENGTH) {
                    _tempPincodeHash = _securityHelper.getHash(pincode);

                    _mode = Mode.REPEAT_PINCODE;

                    _passwordInput.setText("");

                    updateUI();
                }
                break;
            case REPEAT_PINCODE:
                if (pincode.length() == PINCODE_LENGTH) {
                    String newPincodeHash = _securityHelper.getHash(pincode);
                    if (newPincodeHash.equals(_tempPincodeHash)) {
                        registerSubscription(_pincodeUseCase.storePincode(pincode).subscribe(
                                isStored -> {
                                    if (isStored) {
                                        sendBroadcast(Broadcasts.PASSWORD_OK);
                                    }
                                }));
                    } else {
                        _passwordInput.setText("");

                        _pincodeNoMatchText.setVisibility(View.VISIBLE);

                        // TODO allow to start over with new pincode
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_MODE, _mode.ordinal());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        _passwordInput.removeTextChangedListener(_textWatcher);
        _skipButton.setOnClickListener(null);

        super.onDestroyView();
    }
}
