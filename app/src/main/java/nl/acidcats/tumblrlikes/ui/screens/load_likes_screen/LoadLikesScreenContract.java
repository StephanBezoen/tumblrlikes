package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen;

import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenter;
import nl.acidcats.tumblrlikes.ui.screens.base.BaseView;

/**
 * Created on 18/10/2018.
 */
public interface LoadLikesScreenContract {

    interface Presenter extends BasePresenter<LoadLikesScreenContract.View> {
        void onViewCreated();

        void cancelLoading();

        void skipLoading();

        void retryLoading();

        void showSettings();
    }

    interface View extends BaseView {
        void showLoadProgress(int pageCount, long totalPhotoCount);

        void showErrorAlert(int errorStringId);

        void showAllLikesLoaded(long count);

        void showLoadingCancelled();
    }
}
