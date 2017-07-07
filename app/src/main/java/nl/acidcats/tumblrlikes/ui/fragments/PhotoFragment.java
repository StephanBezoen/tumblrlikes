package nl.acidcats.tumblrlikes.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.ui.widgets.InteractiveImageView;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoActionDialog;
import nl.acidcats.tumblrlikes.ui.widgets.PhotoNavBar;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = PhotoFragment.class.getSimpleName();

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
    private Unbinder _unbinder;
    private boolean _isTest = false;
    private Long _photoId;

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LikesApplication) getActivity().getApplication()).getMyComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        _unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            _photoUrl = savedInstanceState.getString(KEY_PHOTO_URL);
            _photoId = savedInstanceState.getLong(KEY_PHOTO_ID);
        }

        _photoView.setGestureListener(this::onGesture);

        if (_isTest) {
            _photoView.setAlpha(0.1f);
        }

        _photoActionDialog.setPhotoRepo(_photoRepo);

        _photoNavBar.setFilterType(_photoRepo.getFilterType());
        _photoNavBar.setFilterOptionSelectionListener(this::setFilterType);

        return view;
    }

    private void setFilterType(FilterType filterType) {
        _photoRepo.setFilterType(filterType);

        showRandomPhoto();
    }

    private void onGesture(InteractiveImageView.Gesture gesture) {
        switch (gesture) {
            case SIDE_SWIPE:
                showRandomPhoto();
                break;
            case TAP:
                showUI();
                break;
            case LONG_PRESS:
                _photoActionDialog.show(_photoId);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        showPhoto();
    }

    private void showPhoto() {
        if (_photoUrl == null) {
            getRandomPhoto();
        }
        if (_photoUrl == null) return;

        String url = _photoUrl;
        if (!_photoUrl.startsWith("http")) url = "file:" + url;

        Glide
                .with(getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new GlideDrawableImageViewTarget(_photoView));

        _photoRepo.startPhotoView(_photoUrl);
    }

    private void getRandomPhoto() {
        if (_photoUrl != null) {
            endPhotoView();
        }

        PhotoEntity photo = _photoRepo.getRandomPhoto();
        if (photo == null) return;

        _photoId = photo.getId();

        _photoUrl = photo.getIsCached() ? photo.getFilePath() : photo.getUrl();
    }

    private void showRandomPhoto() {
        getRandomPhoto();

        showPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photoView.setVisibility(View.VISIBLE);

        _photoRepo.startPhotoView(_photoUrl);

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
            _photoRepo.endPhotoView(_photoUrl);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (_photoUrl != null) {
            outState.putString(KEY_PHOTO_URL, _photoUrl);
        }
        if (_photoId != null) {
            outState.putLong(KEY_PHOTO_ID, _photoId);
        }
    }

    @Override
    public void onDestroy() {
        _photoActionDialog.onDestroy();

        _unbinder.unbind();

        super.onDestroy();
    }
}
