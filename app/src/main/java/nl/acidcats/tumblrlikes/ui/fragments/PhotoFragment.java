package nl.acidcats.tumblrlikes.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import javax.inject.Inject;

import butterknife.BindView;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.Photo;
import nl.acidcats.tumblrlikes.di.AppComponent;
import nl.acidcats.tumblrlikes.ui.widgets.InteractiveImageView;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoActionDialog;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoNavBar;
import nl.acidcats.tumblrlikes.util.GlideApp;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends BaseFragment {

    private static final String KEY_PHOTO_URL = "key_photoUrl";
    private static final String KEY_PHOTO_ID = "key_photoId";

    private static final long HIDE_UI_DELAY_MS = 2000L;

    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.photo)
    InteractiveImageView _photoView;
    @BindView(R.id.photo_action_dialog)
    PhotoActionDialog _photoActionDialog;
    @BindView(R.id.photo_nav_bar)
    PhotoNavBar _photoNavBar;

    private String _photoUrl;
    private Handler _handler = new Handler();
    private Runnable _uiHider = this::hideUI;
    private boolean _isTest = false;
    private Long _photoId;
    private String _photoFallbackUrl;

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
            _photoUrl = savedInstanceState.getString(KEY_PHOTO_URL);
            _photoId = savedInstanceState.getLong(KEY_PHOTO_ID);
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

        _photoActionDialog.setPhotoRepo(_photoRepo);
        _photoActionDialog.setPhotoHiddenListener(this::onPhotoHidden);

        _photoNavBar.setFilterType(_photoRepo.getFilterType());
        _photoNavBar.setFilterOptionSelectionListener(this::setFilterType);

        showPhoto();
    }

    private void onPhotoHidden() {
        showNextPhoto();
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
                _photoActionDialog.show(_photoId);
                break;
            case DOUBLE_TAP:
                _photoView.resetScale();
                break;
        }
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
        if (_photoUrl == null) {
            getNextPhoto();
        }
        if (_photoUrl == null) return;

        String url = _photoUrl;
        if (!_photoUrl.startsWith("http")) url = "file:" + url;

        _photoView.resetScale();

        loadPhoto(url, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadPhoto(_photoFallbackUrl, null);

                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        });

        _photoRepo.startPhotoView(_photoId);
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
        if (_photoUrl != null) {
            endPhotoView();
        }

        Photo photo = _photoRepo.getNextPhoto();
        if (photo == null) return;

        _photoId = photo.id();

        // todo remove _photoUrl completely, store Photo instance instead
        _photoUrl = photo.isCached() ? photo.filePath() : photo.url();
        _photoFallbackUrl = photo.url();
    }

    private void showNextPhoto() {
        getNextPhoto();

        showPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photoView.setVisibility(View.VISIBLE);

        _photoRepo.startPhotoView(_photoId);

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
        if (!_isTest) {
            _photoRepo.endPhotoView(_photoId);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (_photoUrl != null) {
            outState.putString(KEY_PHOTO_URL, _photoUrl);
        }
        if (_photoId != null) {
            outState.putLong(KEY_PHOTO_ID, _photoId);
        }
    }

    @Override
    public void onDestroyView() {
        _photoActionDialog.onDestroyView();
        _photoView.onDestroyView();

        super.onDestroyView();
    }
}
