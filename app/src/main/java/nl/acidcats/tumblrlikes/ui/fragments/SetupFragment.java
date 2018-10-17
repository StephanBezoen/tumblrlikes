package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;

/**
 * Created by stephan on 29/04/2017.
 */

public class SetupFragment extends BaseFragment {
    private static final String TAG = SetupFragment.class.getSimpleName();

    private static final String BLOG_EXT = ".tumblr.com";

    @Inject
    UpdatePhotoCacheUseCase _photoCacheUseCase;
    @Inject
    TumblrBlogUseCase _tumblrBlogUseCase;

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

        _textWatcher = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetupFragment.this.onTextChanged(s.toString());
            }
        };
        _tumblrBlogInput.addTextChangedListener(_textWatcher);

        registerSubscription(
                _tumblrBlogUseCase
                        .getTumblrBlog()
                        .subscribe(tumblrBlog -> {
                            if (tumblrBlog == null && BuildConfig.DEBUG && !TextUtils.isEmpty(BuildConfig.BLOG)) {
                                tumblrBlog = BuildConfig.BLOG;
                            }

                            if (tumblrBlog != null) {
                                if (tumblrBlog.endsWith(BLOG_EXT)) {
                                    tumblrBlog = tumblrBlog.replace(BLOG_EXT, "");
                                }

                                _tumblrBlogInput.setText(tumblrBlog);
                                _tumblrBlogInput.setSelection(tumblrBlog.length());

                                _okButton.setEnabled(true);
                            } else {
                                _okButton.setEnabled(false);
                            }
                        })
        );

        _blogExtensionText.setText(BLOG_EXT);

        _versionText.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        _okButton.setOnClickListener(this::onOkButtonClick);

        _checkCacheButton.setOnClickListener(this::onCheckCacheButtonClick);

        _privacyPolicyButton.setOnClickListener(this::onPrivacyPolicyButtonClick);
    }

    private void onPrivacyPolicyButtonClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL)));
    }

    private void onCheckCacheButtonClick(View view) {
        _checkCacheButton.setEnabled(false);

        registerSubscription(
                _photoCacheUseCase
                        .checkCachedPhotos()
                        .subscribe(
                                this::onCacheChecked,
                                throwable -> {
                                    _checkCacheButton.setEnabled(true);

                                    Log.e(TAG, "onCheckCacheButtonClick: " + throwable.getMessage());
                                })
        );
    }

    private void onCacheChecked(Integer cacheMissCount) {
        _checkCacheButton.setEnabled(true);

        Toast.makeText(getContext(), getString(R.string.cache_miss_count, cacheMissCount.toString()), Toast.LENGTH_SHORT).show();
    }

    private void onOkButtonClick(View view) {
        registerSubscription(_tumblrBlogUseCase.setTumblrBlog(_tumblrBlogInput.getText().toString() + BLOG_EXT).subscribe());

        sendBroadcast(Broadcasts.SETUP_COMPLETE);
    }

    private void onTextChanged(String blog) {
        _okButton.setEnabled(!"".equals(blog));
    }

    @Override
    public void onDestroyView() {
        _tumblrBlogInput.removeTextChangedListener(_textWatcher);

        super.onDestroyView();
    }
}
