package com.mediamonks.mylikes.data.repo.like.store;

import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

import java.util.List;

import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesStore {
    Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime);

    void storeLikes(List<TumblrLikeVO> likes);
}
