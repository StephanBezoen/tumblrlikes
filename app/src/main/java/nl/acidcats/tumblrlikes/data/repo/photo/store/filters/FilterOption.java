package nl.acidcats.tumblrlikes.data.repo.photo.store.filters;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public interface FilterOption {
    PhotoEntity getPhoto(int index);

    long getCount();
}
