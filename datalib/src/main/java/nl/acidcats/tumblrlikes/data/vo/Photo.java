package nl.acidcats.tumblrlikes.data.vo;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created on 12/09/2017.
 */

@AutoValue
public abstract class Photo {
    public abstract long id();

    public abstract long tumblrId();

    @Nullable
    public abstract String filePath();

    @Nullable
    public abstract String url();

    public abstract boolean isFavorite();

    public abstract int likeCount();

    public abstract boolean isCached();

    public abstract int viewCount();

    public static Photo create(String url, long tumblrId) {
        return new AutoValue_Photo(0, tumblrId, null, url, false, 0, false, 0);
    }

    public static Photo create(long id,
                               long tumblrId,
                               @Nullable String filePath,
                               @Nullable String url,
                               boolean isFavorite,
                               int likeCount,
                               boolean isCached,
                               int viewCount) {
        return new AutoValue_Photo(id, tumblrId, filePath, url, isFavorite, likeCount, isCached, viewCount);
    }
}
