package nl.acidcats.tumblrlikes.data.repo.photo.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.FavoriteFilterOptionImpl;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.FilterOption;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.LatestFilterOptionImpl;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.LeastSeenFilterOptionImpl;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.PopularFilterOptionImpl;
import nl.acidcats.tumblrlikes.data.repo.photo.store.filters.UnhiddenFilterOptionImpl;
import nl.acidcats.tumblrlikes.data.vo.db.DaoMaster;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;
import nl.acidcats.tumblrlikes.util.database.DbOpenHelper;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoStoreImpl implements PhotoStore {
    private static final String TAG = PhotoStoreImpl.class.getSimpleName();

    private static final String DATABASE_NAME = "photos.db";

    private CountQuery<PhotoEntity> _countQuery;
    private Query<PhotoEntity> _uncachedQuery;
    private Query<PhotoEntity> _hiddenCachedQuery;
    private final PhotoEntityDao _photoEntityDao;
    private final boolean _debug = BuildConfig.DEBUG;
    private final Map<FilterType, FilterOption> _filters = new HashMap<>();
    private FilterOption _currentFilter;
    private FilterType _currentFilterType;
    private int _runningIndex;
    private List<PhotoEntity> _currentPhotoList;

    public PhotoStoreImpl(Context context) {
        DaoMaster.OpenHelper helper = new DbOpenHelper(context, DATABASE_NAME, null);
        _photoEntityDao = new DaoMaster(helper.getWritableDatabase()).newSession().getPhotoEntityDao();

        initQueries();

        initFilters();
    }

    private void initFilters() {
        _filters.put(FilterType.UNHIDDEN, new UnhiddenFilterOptionImpl(_photoEntityDao));
        _filters.put(FilterType.FAVORITE, new FavoriteFilterOptionImpl(_photoEntityDao));
        _filters.put(FilterType.POPULAR, new PopularFilterOptionImpl(_photoEntityDao));
        _filters.put(FilterType.LATEST, new LatestFilterOptionImpl(_photoEntityDao));
        _filters.put(FilterType.LEAST_SEEN, new LeastSeenFilterOptionImpl(_photoEntityDao));

        _currentFilterType = FilterType.UNHIDDEN;
        _currentFilter = _filters.get(_currentFilterType);
    }

    private void initQueries() {
        // TODO create more cached queries:
        /*
        Query<User> query = userDao.queryBuilder().where(
        Properties.FirstName.eq("Joe"), Properties.YearOfBirth.eq(1970)
        ).build();
        List<User> joesOf1970 = query.list();

        // using the same Query object, we can change the parameters
        // to search for Marias born in 1977 later:
        query.setParameter(0, "Maria");
        query.setParameter(1, 1977);
        List<User> mariasOf1977 = query.list();
        */
        _countQuery = getQueryBuilder().buildCount();

        QueryBuilder<PhotoEntity> builder = getQueryBuilder();
        _uncachedQuery = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(false), PhotoEntityDao.Properties.IsCached.eq(false)))
                .limit(1)
                .build();

        builder = getQueryBuilder();
        _hiddenCachedQuery = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(true), PhotoEntityDao.Properties.IsCached.eq(true)))
                .build();
    }

    private QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder();
    }

    @Override
    public boolean hasPhoto(long postId) {
        List<PhotoEntity> photos = _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.PhotoId.eq(postId)).list();
        return (photos != null) && (photos.size() > 0);
    }

    @Override
    public void storePhotos(List<PhotoEntity> photos) {
        if (_debug) Log.d(TAG, "storePhotos: " + photos.size());

        _photoEntityDao.saveInTx(photos);
    }

    @Override
    public long getPhotoCount() {
        return _countQuery.count();
    }

    @Override
    @Nullable
    public PhotoEntity getNextPhoto() {
        if (_debug) Log.d(TAG, "getNextPhoto: ");

        PhotoEntity photo = _currentFilterType.isRandom() ? getRandomPhoto() : getNextPhotoInLine();

        if (photo != null) {
            // increase view count
            photo.setViewCount(photo.getViewCount() + 1);
            if (_debug) Log.d(TAG, "getNextPhoto: view count now " + photo.getViewCount());
            storePhoto(photo);
        }

        return photo;
    }

    private PhotoEntity getNextPhotoInLine() {
        PhotoEntity photo = _currentPhotoList.get(_runningIndex);

        _runningIndex++;
        if (_runningIndex >= _currentPhotoList.size()) {
            _runningIndex = 0;
        }

        return photo;
    }

    @Nullable
    private PhotoEntity getRandomPhoto() {
        int index = (int) (_currentFilter.getCount() * Math.random());
        if (_debug) Log.d(TAG, "getNextPhoto: index = " + index);

        return _currentFilter.getPhoto(index);
    }

    @Override
    public boolean hasUncachedPhotos() {
        return getNextUncachedPhoto() != null;
    }

    @Override
    @Nullable
    public PhotoEntity getNextUncachedPhoto() {
        return _uncachedQuery.forCurrentThread().unique();
    }

    @Override
    public void storePhoto(PhotoEntity photo) {
        _photoEntityDao.save(photo);
    }

    @Override
    public void addViewTime(PhotoEntity photo, long timeInMs) {
        photo.setViewTime(photo.getViewTime() + timeInMs);
        if (_debug) Log.d(TAG, "addViewTime: view time now " + (timeInMs / 1000) + " s");

        storePhoto(photo);
    }

    @Override
    @Nullable
    public PhotoEntity getPhotoByPath(String filePath) {
        return _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.FilePath.eq(filePath)).limit(1).unique();
    }

    @Override
    public PhotoEntity getPhotoById(long id) {
        return _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.Id.eq(id)).unique();
    }

    @Override
    public void likePhoto(long id) {
        PhotoEntity photo = getPhotoById(id);
        if (photo == null) return;

        photo.setLikeCount(1 + photo.getLikeCount());

        storePhoto(photo);
    }

    @Override
    public void unlikePhoto(long id) {
        PhotoEntity photo = getPhotoById(id);
        if (photo == null) return;

        photo.setLikeCount(photo.getLikeCount() - 1);

        storePhoto(photo);
    }

    @Override
    public void setPhotoFavorite(long id, boolean isFavorite) {
        PhotoEntity photo = getPhotoById(id);
        if (photo == null) return;

        photo.setIsFavorite(isFavorite);

        storePhoto(photo);
    }

    @Override
    public void setPhotoHidden(long id) {
        PhotoEntity photo = getPhotoById(id);
        if (photo == null) return;

        photo.setIsHidden(true);

        storePhoto(photo);
    }

    @Override
    public List<PhotoEntity> getCachedHiddenPhotos() {
        List<PhotoEntity> cachedHiddenPhotos = _hiddenCachedQuery.list();
        if (_debug) Log.d(TAG, "getCachedHiddenPhotos: " + cachedHiddenPhotos.size() + " cached hidden photos found");

        return cachedHiddenPhotos;
    }

    @Override
    public void setFilterType(FilterType filterType) {
        _currentFilterType = filterType;

        _currentFilter = _filters.get(filterType);

        if (filterType.isLinear()) {
            _currentPhotoList = _currentFilter.getAll();
        }

        _runningIndex = 0;
    }

    @Override
    public FilterType getFilterType() {
        return _currentFilterType;
    }
}
