package com.mediamonks.mylikes.data.vo.tumblr;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrResultVO<T> {
    @SerializedName("meta")
    public abstract TumblrMetaVO metaData();

    @SerializedName("response")
    public abstract T response();

    public static <T> TypeAdapter<TumblrResultVO<T>> typeAdapter(Gson gson, TypeToken<? extends TumblrResultVO<T>> typeToken) {
        return new AutoValue_TumblrResultVO.GsonTypeAdapter<T>(gson, typeToken);
    }

}
