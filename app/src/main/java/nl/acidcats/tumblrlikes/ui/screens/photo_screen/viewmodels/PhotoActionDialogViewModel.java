package nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels;

import com.google.auto.value.AutoValue;

/**
 * Created on 19/10/2018.
 */
@AutoValue
public abstract class PhotoActionDialogViewModel {
    public abstract long photoId();

    public abstract boolean isPhotoFavorite();

    public abstract boolean isPhotoLiked();

    public abstract int viewCount();

    public static PhotoActionDialogViewModel create(long id, boolean isFavorite, boolean isLiked, int viewCount) {
        return new AutoValue_PhotoActionDialogViewModel(id, isFavorite, isLiked, viewCount);
    }
}
