package nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created on 19/10/2018.
 */
@AutoValue
public abstract class PhotoFragmentViewModel implements Parcelable {
    public static boolean isValidViewModel(@Nullable PhotoFragmentViewModel viewModel) {
        return viewModel != null && viewModel.url() != null && !"".equals(viewModel.url());
    }

    public abstract long photoId();

    public abstract String url();

    public abstract String fallbackUrl();

    public abstract boolean isFavorite();

    public abstract boolean isLiked();

    public abstract int viewCount();

    public static PhotoFragmentViewModel create(long photoId, String url, String fallbackUrl, boolean isFavorite, boolean isLiked, int viewCount) {
        return new AutoValue_PhotoFragmentViewModel(photoId, url, fallbackUrl, isFavorite, isLiked, viewCount);
    }
}
