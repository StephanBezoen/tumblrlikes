package com.mediamonks.mylikes.data.vo.tumblr;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrMetaVO {
    public abstract int status();

    @SerializedName("msg")
    public abstract String message();

    public static TypeAdapter<TumblrMetaVO> typeAdapter(Gson gson) {
        return new AutoValue_TumblrMetaVO.GsonTypeAdapter(gson);
    }
}
