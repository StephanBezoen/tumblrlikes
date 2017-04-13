package com.mediamonks.mylikes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.mediamonks.mylikes.data.repo.like.LikesRepos;
import com.mediamonks.mylikes.data.repo.photo.PhotoRepo;
import com.mediamonks.mylikes.data.usecase.GetLikesUseCase;
import com.mediamonks.mylikes.data.usecase.StorePhotosUseCase;
import com.mediamonks.mylikes.data.util.PhotoUtil;
import com.mediamonks.mylikes.data.vo.db.PhotoEntity;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    LikesRepos _likesRepos;
    @Inject
    PhotoRepo _photoRepo;

    @BindView(R.id.photo)
    ImageView _photo;
    @BindView(R.id.tv_image_count)
    TextView _imageCountText;

    private int _pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LikesApplication) getApplication()).getMyComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        long time = new Date().getTime() / 1000;
        Log.d(TAG, "onStart: " + time);

        loadLikesPage(time);
    }

    private void loadLikesPage(long time) {
        new GetLikesUseCase(_likesRepos)
                .getLikesPage(time)
                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
                .filter(TumblrLikeVO::isPhoto)
                .filter(tumblrLikeVO -> !_photoRepo.hasPhoto(tumblrLikeVO.id()))
                .map(PhotoUtil::toPhotoEntities)
                .flatMapIterable(photoEntities -> photoEntities)
                .toList()
                .map(photoEntities -> new StorePhotosUseCase(_photoRepo).storePhotos(photoEntities))
                .subscribe(
                        this::handleLikesPageLoaded,
                        throwable -> Log.e(TAG, "onStart: " + throwable.getMessage())
                );
    }

    private void handleLikesPageLoaded(List<PhotoEntity> photoEntities) {
        Log.d(TAG, "showPhoto: " + photoEntities.size() + " likes found");

        _pageCount++;

        long count = _photoRepo.getPhotoCount();
        _imageCountText.setText(getString(R.string.image_count, _pageCount, count));

        Log.d(TAG, "handleLikes: hasMore: " + _likesRepos.hasMoreLikes());
        if (_likesRepos.hasMoreLikes()) {
            Log.d(TAG, "handleLikes: new time: " + _likesRepos.getLastLikeTime());

            loadLikesPage(_likesRepos.getLastLikeTime());
        } else {
            _imageCountText.setText(getString(R.string.all_loaded));
        }

//        Picasso.with(this).load(photos.get(0).originalPhoto().url()).into(_photo);
    }
}
