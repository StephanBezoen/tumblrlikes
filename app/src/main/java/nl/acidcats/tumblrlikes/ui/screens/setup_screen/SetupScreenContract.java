package nl.acidcats.tumblrlikes.ui.screens.setup_screen;

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView;

/**
 * Created on 18/10/2018.
 */
public interface SetupScreenContract {
    String BLOG_EXT = ".tumblr.com";

    interface Presenter extends BasePresenter<View> {
        void onViewCreated();

        void checkCache();

        void onBlogTextChanged(String blog);

        void onSetupDone(String blog);

        void exportPhotos(String filename);
    }

    interface View extends BaseView {
        void setTumblrBlogText(String tumblrBlog);

        void enableCacheCheckButton(boolean enable);

        void enableOkButton(boolean enable);

        void showCacheMissToast(int cacheMissCount);

        void enableExportButton(boolean enable);

        void showExportCompleteToast(boolean success);
    }
}
