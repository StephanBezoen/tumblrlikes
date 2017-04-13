package com.mediamonks.mylikes.data.usecase;

import android.util.Log;

import com.mediamonks.mylikes.BuildConfig;
import com.mediamonks.mylikes.data.repo.like.LikesRepos;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 28/03/2017.
 */

public class GetLikesUseCase {
    private static final String TAG = GetLikesUseCase.class.getSimpleName();

    private static final int LIKES_COUNT = 20;

    private LikesRepos _likesRepos;

    public GetLikesUseCase(LikesRepos likesRepos) {
        _likesRepos = likesRepos;
    }

    public Observable<List<TumblrLikeVO>> getLikesPage(long beforeTime) {
        return _likesRepos
                .getLikes(BuildConfig.BLOG, LIKES_COUNT, beforeTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::logError);

    }

    private void logError(Throwable throwable) {
        Log.e(TAG, "logError: " + throwable.getMessage());
    }
}
