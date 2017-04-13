package com.mediamonks.mylikes.data.vo.tumblr;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.mediamonks.mylikes.data.constants.TumblrPostType;

import java.util.List;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrLikeVO {
    public abstract String type();

    public abstract long id();

    public abstract long timestamp();

    @Nullable
    public abstract List<TumblrPhotoPostVO> photos();

    @SerializedName("post_url")
    public abstract String postUrl();

    public boolean isPhoto() {
        return type().equals(TumblrPostType.PHOTO);
    }



    public static TypeAdapter<TumblrLikeVO> typeAdapter(Gson gson) {
        return new AutoValue_TumblrLikeVO.GsonTypeAdapter(gson);
    }

}
