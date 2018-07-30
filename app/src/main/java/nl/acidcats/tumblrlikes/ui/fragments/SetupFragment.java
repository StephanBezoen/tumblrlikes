package nl.acidcats.tumblrlikes.ui.fragments;

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
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.util.TextWatcherAdapter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by stephan on 29/04/2017.
 */

public class SetupFragment extends BaseFragment {
    private static final String TAG = SetupFragment.class.getSimpleName();

    private static final String BLOG_EXT = ".tumblr.com";

    @Inject
    AppRepo _appRepo;
    @Inject
    PhotoRepo _photoRepo;

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

        String tumblrBlog = _appRepo.getTumblrBlog();
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

        _blogExtensionText.setText(BLOG_EXT);

        _versionText.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        _okButton.setOnClickListener(this::onOkButtonClick);

        _checkCacheButton.setOnClickListener(this::onCheckCacheButtonClick);
    }

    private void onCheckCacheButtonClick(View view) {
        _photoRepo.checkCachedPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCacheChecked, throwable -> Log.e(TAG, "onCheckCacheButtonClick: " + throwable.getMessage()));
    }

    private void onCacheChecked(Integer cacheMissCount) {
        Toast.makeText(getContext(), getString(R.string.cache_miss_count, cacheMissCount.toString()), Toast.LENGTH_SHORT).show();
    }

    private void onOkButtonClick(View view) {
        _appRepo.setTumblrBlog(_tumblrBlogInput.getText().toString() + BLOG_EXT);

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
