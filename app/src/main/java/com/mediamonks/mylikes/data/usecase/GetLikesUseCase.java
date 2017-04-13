package com.mediamonks.mylikes.data.usecase;

import android.util.Log;

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

    private static final int LIKES_COUNT = 5000;

    private LikesRepos _likesRepos;

    public GetLikesUseCase(LikesRepos likesRepos) {
        _likesRepos = likesRepos;
    }

    public Observable<List<TumblrLikeVO>> getLikes(String blogName) {
        return _likesRepos
                .getLikes(blogName, LIKES_COUNT, new Date().getTime())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::logError);

    }

    private void logError(Throwable throwable) {
        Log.e(TAG, "logError: " + throwable.getMessage());
    }
}
