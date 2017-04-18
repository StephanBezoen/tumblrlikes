package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    @Inject
    SecurityHelper _securityHelper;

    @BindView(R.id.input_password)
    EditText _passwordInput;

    private Unbinder _unbinder;
    private TextWatcherAdapter _textWatcher;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        _unbinder = ButterKnife.bind(this, view);

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
    }

    private void onTextChanged(String password) {
        if (_securityHelper.checkPassword(password)) {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.PASSWORD_OK));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onDestroy() {
        _passwordInput.removeTextChangedListener(_textWatcher);

        if (_unbinder != null) {
            _unbinder.unbind();
            _unbinder = null;
        }

        super.onDestroy();
    }
}
