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
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.ui.widgets.InteractiveImageView;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    private static final String KEY_PHOTO_URL = "key_photoUrl";
    private static final long HIDE_UI_DELAY_MS = 2000L;

    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.photo)
    InteractiveImageView _photo;

    private String _photoUrl;
    private Handler _handler = new Handler();
    private Runnable _uiHider = this::hideUI;

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
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            _photoUrl = savedInstanceState.getString(KEY_PHOTO_URL);
        }

        _photo.setGestureListener(this::onGesture);

        return view;
    }

    private void onGesture(InteractiveImageView.Gesture gesture) {
        switch (gesture) {
            case SIDE_SWIPE:
                showRandomPhoto();
                break;
            case TAP:
                showUI();
                break;
        }
    }

    private void showUI() {
        _photo.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        _handler.postDelayed(_uiHider, HIDE_UI_DELAY_MS);

    }

    private void hideUI() {
        _photo.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
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

        Glide
                .with(getContext())
                .load("file:" + _photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new GlideDrawableImageViewTarget(_photo));

        _photoRepo.startPhotoView(_photoUrl);
    }

    private void getRandomPhoto() {
        if (_photoUrl != null) {
            _photoRepo.endPhotoView(_photoUrl);
        }

        PhotoEntity photo = _photoRepo.getRandomPhoto();
        if (photo == null) return;

        _photoUrl = photo.getIsCached() ? photo.getFilePath() : photo.getUrl();
    }

    private void showRandomPhoto() {
        getRandomPhoto();

        showPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photo.setVisibility(View.VISIBLE);

        hideUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        _photoRepo.endPhotoView(_photoUrl);

        _photo.setVisibility(View.INVISIBLE);

        _handler.removeCallbacks(_uiHider);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_PHOTO_URL, _photoUrl);
    }
}
