package com.mediamonks.mylikes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.mediamonks.mylikes.data.repo.like.LikesRepos;
import com.mediamonks.mylikes.data.repo.photo.PhotoRepo;
import com.mediamonks.mylikes.data.usecase.GetLikesUseCase;
import com.mediamonks.mylikes.data.util.PhotoUtil;
import com.mediamonks.mylikes.data.vo.db.PhotoEntity;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((LikesApplication)getApplication()).getMyComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new GetLikesUseCase(_likesRepos)
                .getLikes(getString(R.string.blog))
                .subscribe(
                        this::handleLikes,
                        throwable -> Log.e(TAG, "onStart: " + throwable.getMessage())
                );
//                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
//                .filter(TumblrLikeVO::isPhoto)
//                .filter(tumblrLikeVO -> !_photoRepo.hasPhoto(tumblrLikeVO.id()))
//                .map(tumblrLikeVO -> PhotoUtil.findBiggestPhoto(tumblrLikeVO.id(), tumblrLikeVO.photos()))
//                .toList()
//                .subscribe(
//                        this::handlePhotos,
//                        throwable -> Log.e(TAG, "onStart: " + throwable.getMessage())
//                );
    }

    private void handlePhotos(List<PhotoEntity> photoEntities) {
        Log.d(TAG, "showPhoto: " + photoEntities.size() + " likes found");

//        Picasso.with(this).load(photos.get(0).originalPhoto().url()).into(_photo);
    }

    private void handleLikes(List<TumblrLikeVO> likes) {
        Log.d(TAG, "handleLikes: " + likes.size());
    }
}
