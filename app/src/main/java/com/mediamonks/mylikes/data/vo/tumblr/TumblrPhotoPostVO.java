package com.mediamonks.mylikes.data.vo.tumblr;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */
@AutoValue
public abstract class TumblrPhotoPostVO {
    @SerializedName("original_size")
    public abstract TumblrPhotoVO originalPhoto();

    @SerializedName("alt_sizes")
    public abstract List<TumblrPhotoVO> altPhotos();

    public static TypeAdapter<TumblrPhotoPostVO> typeAdapter(Gson gson) {
        return new AutoValue_TumblrPhotoPostVO.GsonTypeAdapter(gson);
    }

}
