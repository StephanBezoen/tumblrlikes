package com.mediamonks.mylikes.data.repo.like;

import com.mediamonks.mylikes.data.repo.like.store.LikesStore;
import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

import java.util.List;

import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesReposImpl implements LikesRepos {
    private static final String TAG = LikesReposImpl.class.getSimpleName();

    private final LikesStore _netStore;

    public LikesReposImpl(LikesStore netStore) {
        _netStore = netStore;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _netStore.getLikes(blogName, count, beforeTime);
    }
}
