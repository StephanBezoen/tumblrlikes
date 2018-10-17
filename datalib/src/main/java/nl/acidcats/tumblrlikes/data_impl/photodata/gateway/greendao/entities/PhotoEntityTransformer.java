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
    public List<PhotoEntity> transform(List<Photo> photos) {
        List<PhotoEntity> photoEntities = new ArrayList<>();

        for (Photo photo : photos) {
            photoEntities.add(new PhotoEntity(photo.url(), photo.tumblrId()));
        }

        return photoEntities;
    }

    @Nullable
    public Photo transform(@Nullable PhotoEntity photoEntity) {
        if (photoEntity == null) return null;

        return Photo.create(
                photoEntity.getId(),
                photoEntity.getPhotoId(),
                photoEntity.getFilePath(),
                photoEntity.getUrl(),
                photoEntity.getIsFavorite(),
                photoEntity.getIsLiked(),
                photoEntity.getLikeCount(),
                photoEntity.getIsCached(),
                photoEntity.getViewCount(),
                photoEntity.getViewTime(),
                photoEntity.getTimePerView()
        );
    }
}
