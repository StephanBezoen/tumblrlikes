package nl.acidcats.tumblrlikes.ui.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.ui.Broadcasts;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.data_impl.likesdata.LoadLikesException;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.core.usecases.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.di.AppComponent;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoadLikesFragment extends BaseFragment {
    private static final String TAG = LoadLikesFragment.class.getSimpleName();

    @Inject
    LikesDataRepository _likesDataRepository;
    @Inject
    PhotoDataRepository _photoDataRepository;
    @Inject
    AppDataRepository _appDataRepository;
    @Inject
    GetLikesPageUseCase _likesPageUseCase;

    @BindView(R.id.tv_image_count)
    TextView _imageCountText;
    @BindView(R.id.tv_loading)
    TextView _loadingText;
    @BindView(R.id.spinner)
    ProgressBar _spinner;
    @BindView(R.id.btn_cancel)
    Button _cancelButton;

    private int _pageCount;
    private boolean _isLoadingCancelled;

    public static LoadLikesFragment newInstance() {
        return new LoadLikesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loadlikes, container, false);
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            _spinner.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }

        _cancelButton.setOnClickListener(v -> cancelLoading());

        _photoDataRepository
                .removeCachedHiddenPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        uncachedCount -> loadLikesPage(),
                        throwable -> {
                            Log.e(TAG, "onViewCreated: removeCachedHiddenPhotos: " + throwable.getMessage());

                            loadLikesPage();
                        }
                );
    }

    private void cancelLoading() {
        _isLoadingCancelled = true;

        _imageCountText.setText(R.string.loading_cancelled);

        _cancelButton.setEnabled(false);
    }

    private void loadLikesPage() {
        loadLikesPage(new Date().getTime());
    }

    private void loadLikesPage(long time) {
        _likesPageUseCase
                .getPageOfLikesBefore(time)
                .subscribe(this::handleLikesPageLoaded, this::handleError);
    }

    private void handleError(Throwable throwable) {
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

        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error_title)
                    .setMessage(errorStringId)
                    .setPositiveButton(R.string.btn_retry, (dialog, which) -> loadLikesPage())
                    .setNeutralButton(R.string.btn_settings, (dialog, which) -> onSettings())
                    .setNegativeButton(R.string.btn_cancel, (dialog, which) -> onComplete())
                    .create()
                    .show();
        }
    }

    private void onSettings() {
        sendBroadcast(Broadcasts.SETTINGS_REQUEST);
    }

    private void handleLikesPageLoaded(List<Photo> photos) {
        _pageCount++;

        long count = _photoDataRepository.getPhotoCount();

        _imageCountText.setText(getString(R.string.image_page_count, _pageCount, count));

        if (_isLoadingCancelled) {
            onComplete();
        } else {
            if (_likesDataRepository.hasMoreLikes(_appDataRepository.getMostRecentCheckTime())) {
                loadLikesPage(_likesDataRepository.getLastLikeTime());
            } else {
                _imageCountText.setText(getString(R.string.total_image_count, count));
                _loadingText.setText(R.string.all_loaded);

                _appDataRepository.setCheckComplete();

                new Handler().postDelayed(this::onComplete, 500);
            }
        }
    }

    private void onComplete() {
        sendBroadcast(Broadcasts.ALL_LIKES_LOADED);
    }
}
