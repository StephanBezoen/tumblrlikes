package nl.acidcats.tumblrlikes.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.photo)
    ImageView _photo;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        showRandomPhoto();
    }

    private void showRandomPhoto() {
        PhotoEntity photo = _photoRepo.getRandomPhoto();
        if (photo == null) return;

        String url = photo.getIsCached() ? photo.getFilePath() : photo.getUrl();
        Log.d(TAG, "showRandomPhoto: url = " + url);
        Picasso.with(getContext()).load(url).noPlaceholder().into(_photo);
    }

    @OnClick(R.id.photo)
    void onPhotoClick() {
        showRandomPhoto();
    }

    @Override
    public void onResume() {
        super.onResume();

        _photo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        _photo.setVisibility(View.INVISIBLE);
    }
}
