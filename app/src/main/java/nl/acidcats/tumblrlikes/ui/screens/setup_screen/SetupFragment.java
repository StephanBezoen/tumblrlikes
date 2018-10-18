package nl.acidcats.tumblrlikes.ui.screens.setup_screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;

/**
 * Created by stephan on 29/04/2017.
 */

public class SetupFragment extends BaseFragment implements SetupScreenContract.View {
    private static final String TAG = SetupFragment.class.getSimpleName();

    @Inject
    SetupScreenContract.Presenter _presenter;

    @BindView(R.id.input_tumblr_blog)
    EditText _tumblrBlogInput;
    @BindView(R.id.btn_ok)
    TextView _okButton;
    @BindView(R.id.blog_ext_txt)
    TextView _blogExtensionText;
    @BindView(R.id.txt_version)
    TextView _versionText;
    @BindView(R.id.btn_check_cache)
    TextView _checkCacheButton;
    @BindView(R.id.btn_privacy_policy)
    TextView _privacyPolicyButton;

    private TextWatcherAdapter _textWatcher;

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _presenter.setView(this);
        _presenter.onViewCreated();

        _textWatcher = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _presenter.onBlogTextChanged(s.toString());
            }
        };
        _tumblrBlogInput.addTextChangedListener(_textWatcher);

        _blogExtensionText.setText(SetupScreenContract.BLOG_EXT);

        _versionText.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        _okButton.setOnClickListener(this::onOkButtonClick);

        _checkCacheButton.setOnClickListener(this::onCheckCacheButtonClick);

        _privacyPolicyButton.setOnClickListener(this::onPrivacyPolicyButtonClick);
    }

    @Override
    public void setTumblrBlogText(String tumblrBlog) {
        _tumblrBlogInput.setText(tumblrBlog);
        _tumblrBlogInput.setSelection(tumblrBlog.length());
    }

    @Override
    public void enableCacheCheckButton(boolean enable) {
        _checkCacheButton.setEnabled(enable);
    }

    @Override
    public void enableOkButton(boolean enable) {
        _okButton.setEnabled(enable);
    }

    private void onPrivacyPolicyButtonClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL)));
    }

    private void onCheckCacheButtonClick(View view) {
        _presenter.checkCache();
    }

    private void onOkButtonClick(View view) {
        _presenter.onSetupDone(_tumblrBlogInput.getText().toString() + SetupScreenContract.BLOG_EXT);
    }

    @Override
    public void showCacheMissToast(Integer cacheMissCount) {
        Toast.makeText(getContext(), getString(R.string.cache_miss_count, cacheMissCount.toString()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        _tumblrBlogInput.removeTextChangedListener(_textWatcher);

        _presenter.onDestroyView();

        super.onDestroyView();
    }
}