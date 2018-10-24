package nl.acidcats.tumblrlikes.ui.screens.load_likes_screen;

import android.os.Handler;
import android.support.annotation.StringRes;
import android.util.Log;

import java.util.Date;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode;
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoCacheUseCase;
import nl.acidcats.tumblrlikes.data_impl.likesdata.LoadLikesException;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl;

/**
 * Created on 18/10/2018.
 */
public class LoadLikesScreenPresenter extends BasePresenterImpl<LoadLikesScreenContract.View> implements LoadLikesScreenContract.Presenter {
    private static final String TAG = LoadLikesScreenPresenter.class.getSimpleName();

    @Inject
    GetLikesPageUseCase _likesPageUseCase;
    @Inject
    UpdatePhotoCacheUseCase _photoCacheUseCase;

    private int _pageCount;
    private boolean _isLoadingCancelled;

    @Inject
    LoadLikesScreenPresenter() {
    }

    @Override
    public void onViewCreated() {
        _photoCacheUseCase
                .removeCachedHiddenPhotos()
                .subscribe(
                        hasRemoved -> startLoadingLikes(),
                        throwable -> {
                            Log.e(TAG, "onViewCreated: removeCachedHiddenPhotos: " + throwable.getMessage());

                            startLoadingLikes();
                        }
                );
    }

    private void startLoadingLikes() {
        loadLikesPage(LoadLikesMode.FRESH);
    }

    private void loadLikesPage(LoadLikesMode mode) {
        registerSubscription(
                _likesPageUseCase
                        .loadLikesPage(mode)
                        .subscribe(this::handleLikesPageLoaded, this::handleLoadPageError)
        );
    }

    private void handleLikesPageLoaded(long totalPhotoCount) {
        if (_isLoadingCancelled) {
            notifyLoadingComplete();

            return;
        }

        _pageCount++;

        registerSubscription(
                _likesPageUseCase
                        .checkLoadLikesComplete(new Date().getTime())
                        .subscribe(isComplete -> {
                            if (isComplete) {
                                onLoadComplete(totalPhotoCount);
                            } else {
                                if (getView() != null) {
                                    getView().showLoadProgress(_pageCount, totalPhotoCount);
                                }

                                loadLikesPage(LoadLikesMode.CONTINUED);
                            }
                        }, throwable -> Log.e(TAG, "handleLikesPageLoaded: " + throwable.getMessage()))
        );
    }

    private void onLoadComplete(long totalPhotoCount) {
        if (getView() != null) {
            getView().showAllLikesLoaded(totalPhotoCount);
        }

        new Handler().postDelayed(this::notifyLoadingComplete, 500);
    }


    private void handleLoadPageError(Throwable throwable) {
        Log.e(TAG, "handleError: " + throwable.getMessage());

        @StringRes int errorStringId = R.string.error_load;

        if (throwable instanceof LoadLikesException) {
            LoadLikesException exception = (LoadLikesException) throwable;
            if (exception.getCode() == 403) {
                errorStringId = R.string.error_403;
            } else if (exception.getCode() == 404) {
                errorStringId = R.string.error_404;
            } else if (exception.getCode() >= 300 && exception.getCode() < 500) {
                errorStringId = R.string.error_300_400;
            } else if (exception.getCode() >= 500 && exception.getCode() < 600) {
                errorStringId = R.string.error_500;
            }
        }

        if (getView() != null) {
            getView().showErrorAlert(errorStringId);
        }
    }

    private void notifyLoadingComplete() {
        notify(Broadcasts.ALL_LIKES_LOADED);
    }

    @Override
    public void cancelLoading() {
        _isLoadingCancelled = true;

        if (getView() != null) {
            getView().showLoadingCancelled();
        }
    }

    @Override
    public void skipLoading() {
        notifyLoadingComplete();
    }

    @Override
    public void retryLoading() {
        startLoadingLikes();
    }

    @Override
    public void showSettings() {
        notify(Broadcasts.SETTINGS_REQUEST);
    }
}
