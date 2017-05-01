package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepo;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private static final String KEY_MODE = "key_mode";
    private static final int PINCODE_LENGTH = 4;

    public enum Mode {
        NEW_PINCODE, REPEAT_PINCODE, LOGIN
    }

    @Inject
    SecurityHelper _securityHelper;
    @Inject
    AppRepo _appRepo;

    @BindView(R.id.input_password)
    EditText _passwordInput;
    @BindView(R.id.tv_pincode_header)
    TextView _header;
    @BindView(R.id.btn_skip)
    View _skipButton;

    private Unbinder _unbinder;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MODE)) {
            _mode = Mode.values()[savedInstanceState.getInt(KEY_MODE)];
        } else if (args != null && args.containsKey(KEY_MODE)){
            _mode = Mode.values()[args.getInt(KEY_MODE)];
        }

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        _unbinder = ButterKnife.bind(this, view);

        InputFilter filter = new InputFilter.LengthFilter(PINCODE_LENGTH);
        _passwordInput.setFilters(new InputFilter[]{filter});

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        // TODO implement skipped pincode setup
    }

    private void onTextChanged(String pincode) {
        switch (_mode) {
            case LOGIN:
                if (_appRepo.isPincodeCorrect(pincode)) {
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.PASSWORD_OK));
                }
                break;
            case NEW_PINCODE:
                if (pincode.length() == PINCODE_LENGTH) {
                    _tempPincodeHash = _securityHelper.getHash(pincode);

                    _mode = Mode.REPEAT_PINCODE;

                    updateUI();
                }
                break;
            case REPEAT_PINCODE:
                if (pincode.length() == PINCODE_LENGTH) {
                    String newPincodeHash = _securityHelper.getHash(pincode);
                    if (newPincodeHash.equals(_tempPincodeHash)) {
                        _appRepo.setPinCode(pincode);

                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.PASSWORD_OK));
                    } else {
                        _passwordInput.setText("");

                        // TODO show error message
                        // TODO allow to start over with new pincode
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_MODE, _mode.ordinal());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        _passwordInput.removeTextChangedListener(_textWatcher);
        _skipButton.setOnClickListener(null);

        if (_unbinder != null) {
            _unbinder.unbind();
            _unbinder = null;
        }

        super.onDestroy();
    }
}
