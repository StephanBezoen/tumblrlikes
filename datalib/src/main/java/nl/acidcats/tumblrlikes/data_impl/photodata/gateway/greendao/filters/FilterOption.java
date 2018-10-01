package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters;

import java.util.List;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;

/**
 * Created by stephan on 17/05/2017.
 */

public interface FilterOption {
    PhotoEntity getPhoto(int index);

    List<PhotoEntity> getAll();

    long getCount();
}
