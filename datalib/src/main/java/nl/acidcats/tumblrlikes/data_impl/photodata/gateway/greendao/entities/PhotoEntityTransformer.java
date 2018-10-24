package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import nl.acidcats.tumblrlikes.core.models.Photo;

/**
 * Created on 17/10/2018.
 */
public class PhotoEntityTransformer {

    @NonNull
    public List<PhotoEntity> toPhotoEntity(List<Photo> photos) {
        List<PhotoEntity> photoEntities = new ArrayList<>();

        for (Photo photo : photos) {
            photoEntities.add(new PhotoEntity(photo.getUrl(), photo.getTumblrId()));
        }

        return photoEntities;
    }

    @Nullable
    public Photo toPhoto(@Nullable PhotoEntity photoEntity) {
        if (photoEntity == null) return null;

        return new Photo(
                photoEntity.getId(),
                photoEntity.getPhotoId(),
                photoEntity.getFilePath(),
                photoEntity.getUrl(),
                photoEntity.getIsFavorite(),
                photoEntity.getIsLiked(),
                photoEntity.getIsCached(),
                photoEntity.getViewCount(),
                photoEntity.getViewTime(),
                photoEntity.getTimePerView()
        );
    }

    @NonNull
    public List<Photo> toPhotos(@Nullable List<PhotoEntity> photoEntities) {
        List<Photo> photos = new ArrayList<>();

        if (photoEntities != null) {
            for (PhotoEntity photoEntity : photoEntities) {
                photos.add(toPhoto(photoEntity));
            }
        }

        return photos;
    }
}
