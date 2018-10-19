package nl.acidcats.tumblrlikes.ui.screens.setup_screen;

import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl;

import static nl.acidcats.tumblrlikes.ui.screens.setup_screen.SetupScreenContract.BLOG_EXT;

/**
 * Created on 18/10/2018.
 */
public class SetupScreenPresenter extends BasePresenterImpl<SetupScreenContract.View> implements SetupScreenContract.Presenter {
    private static final String TAG = SetupScreenPresenter.class.getSimpleName();

    @Inject
    UpdatePhotoCacheUseCase _photoCacheUseCase;
    @Inject
    TumblrBlogUseCase _tumblrBlogUseCase;

    @Inject
    SetupScreenPresenter() {
    }

    @Override
    public void onViewCreated() {
        registerSubscription(
                _tumblrBlogUseCase
                        .getTumblrBlog()
                        .subscribe(tumblrBlog -> {
                            if (tumblrBlog == null && BuildConfig.DEBUG && !TextUtils.isEmpty(BuildConfig.BLOG)) {
                                tumblrBlog = BuildConfig.BLOG;
                            }

                            if (getView() != null) {
                                if (tumblrBlog != null) {
                                    if (tumblrBlog.endsWith(BLOG_EXT)) {
                                        tumblrBlog = tumblrBlog.replace(BLOG_EXT, "");
                                    }

                                    getView().setTumblrBlogText(tumblrBlog);
                                    getView().enableOkButton(true);
                                } else {
                                    getView().setTumblrBlogText("");
                                    getView().enableOkButton(false);
                                }
                            }
                        })
        );
    }

    @Override
    public void checkCache() {
        if (getView() != null) {
            getView().enableCacheCheckButton(false);
        }

        registerSubscription(
                _photoCacheUseCase
                        .checkCachedPhotos()
                        .subscribe(
                                this::onCacheChecked,
                                throwable -> {
                                    if (getView() != null) {
                                        getView().enableCacheCheckButton(true);
                                    }

                                    Log.e(TAG, "onCheckCacheButtonClick: " + throwable.getMessage());
                                })
        );
    }

    private void onCacheChecked(Integer cacheMissCount) {
        if (getView() == null) return;

        getView().enableCacheCheckButton(true);

        getView().showCacheMissToast(cacheMissCount);
    }

    @Override
    public void onBlogTextChanged(String blog) {
        if (getView() != null) {
            getView().enableOkButton(!"".equals(blog));
        }
    }

    @Override
    public void onSetupDone(String blog) {
        if (getView() != null) {
            getView().enableOkButton(false);
        }

        registerSubscription(
                _tumblrBlogUseCase
                        .setTumblrBlog(blog)
                        .subscribe(ignored -> notify(Broadcasts.SETUP_COMPLETE)));
    }
}
