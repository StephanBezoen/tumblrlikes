package nl.acidcats.tumblrlikes.data.repo.photo.store.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;

/**
 * Created on 03/08/2017.
 */

public class LeastSeenFilterOptionImpl extends AbstractFilterOptionImpl {
    public LeastSeenFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder().orderAsc(PhotoEntityDao.Properties.ViewCount);
    }
}
