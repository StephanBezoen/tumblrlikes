package nl.acidcats.tumblrlikes.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 13/04/2017.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = PhotoFragment.class.getSimpleName();

    private static final String KEY_PHOTO_URL = "key_photoUrl";

    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.photo)
    ImageView _photo;

    private String _photoUrl;

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

        return view;
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

        Glide.with(getContext()).load(_photoUrl).into(new GlideDrawableImageViewTarget(_photo));

        _photoRepo.startPhotoView(_photoUrl);
    }

    private void getRandomPhoto() {
        if (_photoUrl != null) {
            _photoRepo.endPhotoView(_photoUrl);
        }

        PhotoEntity photo = _photoRepo.getRandomPhoto();
        if (photo == null) return;

        _photoUrl = photo.getIsCached() ? "file:" + photo.getFilePath() : photo.getUrl();
    }

    @OnClick(R.id.photo)
    void onPhotoClick() {
        getRandomPhoto();

        showPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        _photoRepo.endPhotoView(_photoUrl);

        _photo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_PHOTO_URL, _photoUrl);
    }
}
