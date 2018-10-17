package nl.acidcats.tumblrlikes.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.auto.value.AutoValue;

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoPropertyUseCase;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.widgets.InteractiveImageView;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoActionDialog;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoNavBar;
import nl.acidcats.tumblrlikes.util.GlideApp;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends BaseFragment {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    private static final String KEY_VIEW_MODEL = "key_" + TAG + "_viewModel";

    private static final long HIDE_UI_DELAY_MS = 2000L;

    @Inject
    PhotoDataRepository _photoRepo;
    @Inject
    UpdatePhotoPropertyUseCase _updatePhotoPropertyUseCase;

    @BindView(R.id.photo)
    InteractiveImageView _photoView;
    @BindView(R.id.photo_action_dialog)
    PhotoActionDialog _photoActionDialog;
    @BindView(R.id.photo_nav_bar)
    PhotoNavBar _photoNavBar;

    private Handler _handler = new Handler();
    private Runnable _uiHider = this::hideUI;
    private boolean _isTest = true;
    private PhotoFragmentViewModel _viewModel;

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    protected void injectFrom(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _viewModel = savedInstanceState.getParcelable(KEY_VIEW_MODEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _photoView.setGestureListener(this::onGesture);

        if (_isTest) {
            _photoView.setAlpha(0.1f);
        }

        _photoActionDialog.setPhotoActionListener(new PhotoActionDialog.PhotoActionListener() {
            @Override
            public void onHidePhoto(long id) {
                registerSubscription(
                        _updatePhotoPropertyUseCase
                                .setHidden(id)
                                .subscribe(photo -> {
                                    _photoActionDialog.hide(PhotoActionDialog.HideFlow.INSTANT);

                                    showNextPhoto();
                                })
                );
            }

            @Override
            public void onUpdatePhotoLike(long id, boolean isLiked) {
                registerSubscription(
                        _updatePhotoPropertyUseCase
                                .updateLike(id, isLiked)
                                .subscribe(photo -> onPhotoPropertyUpdated(photo))
                );
            }

            @Override
            public void onUpdatePhotoFavorite(long id, boolean isFavorite) {
                registerSubscription(
                        _updatePhotoPropertyUseCase
                                .updateFavorite(id, isFavorite)
                                .subscribe(photo -> onPhotoPropertyUpdated(photo)));
            }
        });

        _photoNavBar.setFilterType(_photoRepo.getFilterType());
        _photoNavBar.setFilterOptionSelectionListener(this::setFilterType);

        showPhoto();
    }

    private void onPhotoPropertyUpdated(Photo photo) {
        createViewModel(photo);

        _photoActionDialog.updateViewModel(createPhotoActionDialogViewModel());

        _photoActionDialog.hide(PhotoActionDialog.HideFlow.ANIMATED);
    }

    private void setFilterType(FilterType filterType) {
        _photoRepo.setFilterType(filterType);

        showNextPhoto();
    }

    private void onGesture(InteractiveImageView.Gesture gesture) {
        switch (gesture) {
            case SIDE_SWIPE:
                showNextPhoto();
                break;
            case TAP:
                showUI();
                break;
            case LONG_PRESS:
                _photoActionDialog.show(createPhotoActionDialogViewModel());
                break;
            case DOUBLE_TAP:
                _photoView.resetScale();
                break;
        }
    }

    @NonNull
    private PhotoActionDialog.PhotoActionDialogViewModel createPhotoActionDialogViewModel() {
        return PhotoActionDialog.PhotoActionDialogViewModel.create(
                _viewModel.photoId(),
                _viewModel.isFavorite(),
                _viewModel.isLiked(),
                _viewModel.viewCount());
    }

    private void showUI() {
        _photoView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        _handler.removeCallbacks(_uiHider);
        _handler.postDelayed(_uiHider, HIDE_UI_DELAY_MS);

        _photoNavBar.show();
    }

    private void hideUI() {
        _photoView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        _photoNavBar.hide();
    }

    private void showPhoto() {
        if (_viewModel == null || _viewModel.url() == null) {
            getNextPhoto();
        }
        if (_viewModel == null || _viewModel.url() == null) return;

        _photoView.resetScale();

        loadPhoto(_viewModel.url(), new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadPhoto(_viewModel.fallbackUrl(), null);

                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        });

        _photoRepo.startPhotoView(_viewModel.photoId());
    }

    private void loadPhoto(String url, @Nullable RequestListener<Drawable> listener) {
        GlideApp.with(getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(listener)
                .into(new DrawableImageViewTarget(_photoView));
    }

    private void getNextPhoto() {
        if (_viewModel != null && _viewModel.url() != null) {
            endPhotoView();
        }

        Photo photo = _photoRepo.getNextPhoto();
        if (photo == null) return;

        createViewModel(photo);
    }

    private void createViewModel(Photo photo) {
        String url = photo.isCached() ? photo.filePath() : photo.url();
        if (url != null && !url.startsWith("http")) url = "file:" + url;

        _viewModel = PhotoFragmentViewModel.create(
                photo.id(),
                url,
                photo.url(),
                photo.isFavorite(),
                photo.likeCount() > 0,
                photo.viewCount());
    }

    private void showNextPhoto() {
        getNextPhoto();

        showPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photoView.setVisibility(View.VISIBLE);

        _photoRepo.startPhotoView(_viewModel.photoId());

        hideUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        endPhotoView();

        _photoView.setVisibility(View.INVISIBLE);

        _handler.removeCallbacks(_uiHider);
    }

    private void endPhotoView() {
        _photoRepo.endPhotoView(_viewModel.photoId());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (_viewModel != null) {
            outState.putParcelable(KEY_VIEW_MODEL, _viewModel);
        }
    }

    @Override
    public void onDestroyView() {
        _photoActionDialog.onDestroyView();
        _photoView.onDestroyView();

        super.onDestroyView();
    }

    @AutoValue
    static abstract class PhotoFragmentViewModel implements Parcelable {
        abstract long photoId();

        abstract String url();

        abstract String fallbackUrl();

        abstract boolean isFavorite();

        abstract boolean isLiked();

        abstract int viewCount();

        static PhotoFragmentViewModel create(long photoId, String url, String fallbackUrl, boolean isFavorite, boolean isLiked, int viewCount) {
            return new AutoValue_PhotoFragment_PhotoFragmentViewModel(photoId, url, fallbackUrl, isFavorite, isLiked, viewCount);
        }
    }
}
