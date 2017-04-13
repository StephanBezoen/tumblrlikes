package com.mediamonks.mylikes.data.repo.like;

import com.mediamonks.mylikes.data.vo.tumblr.TumblrLikeVO;

import java.util.List;

import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesRepos {
    Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime);

    boolean hasMoreLikes();

    long getLastLikeTime();
}
