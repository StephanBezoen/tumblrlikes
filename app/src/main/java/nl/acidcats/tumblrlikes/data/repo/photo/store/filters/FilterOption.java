package nl.acidcats.tumblrlikes.data.repo.photo.store.filters;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 17/05/2017.
 */

public interface FilterOption {
    PhotoEntity getPhoto(int index);

    long getCount();
}
