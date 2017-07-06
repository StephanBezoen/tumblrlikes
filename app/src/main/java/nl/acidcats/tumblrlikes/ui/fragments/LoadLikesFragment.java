package nl.acidcats.tumblrlikes.ui.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.usecase.GetLikesPageUseCase;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by stephan on 13/04/2017.
 */

public class LoadLikesFragment extends Fragment {
    private static final String TAG = LoadLikesFragment.class.getSimpleName();

    @Inject
    LikesRepo _likesRepo;
    @Inject
    PhotoRepo _photoRepo;
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
    private Unbinder _unbinder;
    private boolean _isLoadingCancelled;

    public static LoadLikesFragment newInstance() {
        return new LoadLikesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadlikes, container, false);
        _unbinder = ButterKnife.bind(this, view);

        _spinner.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        _cancelButton.setOnClickListener(v ->  cancelLoading());

        loadLikesPage();
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

        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            if (exception.code() == 403) {
                errorStringId = R.string.error_403;
            } else if (exception.code() == 404) {
                errorStringId = R.string.error_404;
            } else if (exception.code() >= 300 && exception.code() < 500){
                errorStringId = R.string.error_300_400;
            } else if (exception.code() >= 500 && exception.code() < 600){
                errorStringId = R.string.error_500;
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error_title)
                .setMessage(errorStringId)
                .setPositiveButton(R.string.btn_retry, (dialog, which) -> loadLikesPage())
                .setNeutralButton(R.string.btn_settings, (dialog, which) -> onSettings())
                .setNegativeButton(R.string.btn_cancel, (dialog, which) -> onComplete())
                .create()
                .show();
    }

    private void onSettings() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.SETTINGS_REQUEST));
    }

    private void handleLikesPageLoaded(List<PhotoEntity> photoEntities) {
        _pageCount++;

        long count = _photoRepo.getPhotoCount();

        _imageCountText.setText(getString(R.string.image_page_count, _pageCount, count));

        if (_isLoadingCancelled) {
            onComplete();
        } else {
            if (_likesRepo.hasMoreLikes()) {
                loadLikesPage(_likesRepo.getLastLikeTime());
            } else {
                _imageCountText.setText(getString(R.string.total_image_count, count));
                _loadingText.setText(R.string.all_loaded);

                _likesRepo.setCheckComplete();

                new Handler().postDelayed(this::onComplete, 500);
            }
        }
    }

    private void onComplete() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Broadcasts.ALL_LIKES_LOADED));
    }

    @Override
    public void onDestroy() {
        if (_unbinder != null) {
            _unbinder.unbind();
            _unbinder = null;
        }

        super.onDestroy();
    }
}
