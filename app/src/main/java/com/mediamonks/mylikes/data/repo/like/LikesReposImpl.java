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
    private boolean _hasMore;
    private long _lastTime;

    public LikesReposImpl(LikesStore netStore) {
        _netStore = netStore;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _netStore.getLikes(blogName, count, beforeTime).doOnNext(tumblrLikeVOs -> {
            if (tumblrLikeVOs.size() == 0) {
                _hasMore = false;
            } else {
                _hasMore = true;

                _lastTime = tumblrLikeVOs.get(tumblrLikeVOs.size() - 1).timestamp();
            }
        });
    }

    @Override
    public boolean hasMoreLikes() {
        return _hasMore;
    }

    @Override
    public long getLastLikeTime() {
        return _lastTime;
    }
}
