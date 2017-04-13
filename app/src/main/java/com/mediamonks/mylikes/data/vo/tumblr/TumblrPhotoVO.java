package com.mediamonks.mylikes.data.vo.tumblr;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by stephan on 11/04/2017.
 */
@AutoValue
public abstract class TumblrPhotoVO {
    public abstract String url();

    public abstract int width();

    public abstract int height();

    public long getSize() {
        return (long)width() * (long)height();
    }

    public static TypeAdapter<TumblrPhotoVO> typeAdapter(Gson gson) {
        return new AutoValue_TumblrPhotoVO.GsonTypeAdapter(gson);
    }


}
