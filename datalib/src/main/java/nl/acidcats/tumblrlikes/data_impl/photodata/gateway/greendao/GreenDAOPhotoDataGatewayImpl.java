package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.DbOpenHelper;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntityTransformer;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FavoriteFilterOptionImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FilterOption;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.LatestFilterOptionImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.LeastSeenFilterOptionImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.PopularFilterOptionImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.UnhiddenFilterOptionImpl;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators.AbstractPhotoIterator;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators.LinearPhotoIterator;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators.RandomPhotoIterator;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.db_impl_greendao.DaoMaster;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created by stephan on 11/04/2017.
 */

public class GreenDAOPhotoDataGatewayImpl implements PhotoDataGateway {
    private static final String TAG = GreenDAOPhotoDataGatewayImpl.class.getSimpleName();

    private static final String DATABASE_NAME = "photos.db";

    private CountQuery<PhotoEntity> _countQuery;
    private Query<PhotoEntity> _uncachedQuery;
    private Query<PhotoEntity> _hiddenCachedQuery;
    private final PhotoEntityDao _photoEntityDao;
    private final boolean _debug = BuildConfig.DEBUG;
    private final Map<FilterType, FilterOption> _filters = new HashMap<>();
    private Iterator<PhotoEntity> _photoIterator;
    private final PhotoEntityTransformer _photoEntityTransformer = new PhotoEntityTransformer();

    public GreenDAOPhotoDataGatewayImpl(Context context) {
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
        _countQuery = createQueryBuilder().buildCount();

        QueryBuilder<PhotoEntity> builder = createQueryBuilder();
        _uncachedQuery = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(false), PhotoEntityDao.Properties.IsCached.eq(false)))
                .limit(1)
                .build();

        builder = createQueryBuilder();
        _hiddenCachedQuery = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(true), PhotoEntityDao.Properties.IsCached.eq(true)))
                .build();
    }

    private QueryBuilder<PhotoEntity> createQueryBuilder() {
        return _photoEntityDao.queryBuilder();
    }

    @Override
    public boolean hasPhoto(long postId) {
        List<PhotoEntity> photos = createQueryBuilder().where(PhotoEntityDao.Properties.PhotoId.eq(postId)).list();
        return (photos != null) && (photos.size() > 0);
    }

    @Override
    public void storePhotos(List<Photo> photos) {
        if (_debug) Log.d(TAG, "storePhotos: " + photos.size());

        _photoEntityDao.saveInTx(_photoEntityTransformer.transform(photos));
    }

    @Override
    public long getPhotoCount() {
        return _countQuery.count();
    }

    @Override
    @Nullable
    public Photo getNextPhoto() {
        if (_debug) Log.d(TAG, "getNextPhoto: ");

        PhotoEntity photoEntity = _photoIterator.next();

        if (photoEntity != null) {
            // increase view count
            photoEntity.setViewCount(photoEntity.getViewCount() + 1);
            if (_debug) Log.d(TAG, "getNextPhoto: view count now " + photoEntity.getViewCount());
            storePhoto(photoEntity);

            return _photoEntityTransformer.transform(photoEntity);
        }

        return null;
    }

    @Override
    public boolean hasUncachedPhotos() {
        return getUncachedPhoto() != null;
    }

    @Override
    @Nullable
    public Photo getUncachedPhoto() {
        PhotoEntity photoEntity = _uncachedQuery.forCurrentThread().unique();
        return _photoEntityTransformer.transform(photoEntity);
    }

    private void storePhoto(PhotoEntity photo) {
        _photoEntityDao.save(photo);
    }

    @Override
    public void setPhotoCached(long id, boolean isCached, @Nullable String filepath) {
        PhotoEntity photoEntity = getPhotoEntityById(id);
        if (photoEntity == null) return;

        photoEntity.setIsCached(isCached);
        photoEntity.setFilePath(filepath);

        storePhoto(photoEntity);
    }

    @Override
    public void setPhotosCached(List<Long> ids, boolean isCached) {
        final int totalCount = ids.size();
        final int maxPerPage = 500;
        final int pageCount = (int) Math.ceil((float) totalCount / (float) maxPerPage);
        final List<Long> idsPage = new ArrayList<>();

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            idsPage.clear();

            final int baseIndex = pageIndex * maxPerPage;
            final int endIndex = Math.min(baseIndex + maxPerPage, totalCount);
            for (int idIndex = baseIndex; idIndex < endIndex; idIndex++) {
                idsPage.add(ids.get(idIndex));
            }

            List<PhotoEntity> photoEntities = createQueryBuilder().where(PhotoEntityDao.Properties.Id.in(idsPage)).list();
            for (PhotoEntity photoEntity : photoEntities) {
                photoEntity.setIsCached(isCached);
            }

            _photoEntityDao.saveInTx(photoEntities);
        }

        if (_debug) Log.d(TAG, "setAsUncached: " + ids.size() + " photos uncached");
    }

    @Override
    public void addPhotoViewTime(long id, long timeInMs) {
        PhotoEntity photoEntity = getPhotoEntityById(id);
        if (photoEntity == null) return;

        photoEntity.setViewTime(photoEntity.getViewTime() + timeInMs);
        if (_debug) Log.d(TAG, "addViewTime: view time now " + (timeInMs / 1000) + " s");

        storePhoto(photoEntity);
    }

    @Override
    public Photo getPhotoById(long id) {
        PhotoEntity photoEntity = getPhotoEntityById(id);
        return _photoEntityTransformer.transform(photoEntity);
    }

    @Nullable
    private PhotoEntity getPhotoEntityById(long id) {
        return createQueryBuilder().where(PhotoEntityDao.Properties.Id.eq(id)).unique();
    }

    @Override
    public void setPhotoLiked(long id, boolean isLiked) {
        PhotoEntity photo = getPhotoEntityById(id);
        if (photo == null) return;

        int currentLikeCount = photo.getLikeCount();
        int newLikeCount = isLiked ? 1 : 0;
        if (currentLikeCount == newLikeCount) return;

        photo.setLikeCount(newLikeCount);
        storePhoto(photo);
    }

    @Override
    public void setPhotoFavorite(long id, boolean isFavorite) {
        PhotoEntity photo = getPhotoEntityById(id);
        if (photo == null) return;

        photo.setIsFavorite(isFavorite);

        storePhoto(photo);
    }

    @Override
    public void setPhotoHidden(long id) {
        PhotoEntity photo = getPhotoEntityById(id);
        if (photo == null) return;

        photo.setIsHidden(true);

        storePhoto(photo);
    }

    @Override
    public List<Photo> getCachedHiddenPhotos() {
        List<PhotoEntity> cachedHiddenPhotos = _hiddenCachedQuery.list();
        if (_debug) Log.d(TAG, "getCachedHiddenPhotos: " + cachedHiddenPhotos.size() + " cached hidden photos found");

        List<Photo> photos = new ArrayList<>();
        for (PhotoEntity photoEntity : cachedHiddenPhotos) {
            photos.add(_photoEntityTransformer.transform(photoEntity));
        }

        return photos;
    }

    @Override
    public void initFilter(FilterType filterType) {
        _photoIterator = filterType.isLinear() ? new LinearPhotoIterator() : new RandomPhotoIterator();

        ((AbstractPhotoIterator) _photoIterator).setFilterOption(_filters.get(filterType));
    }

    @Override
    public List<Photo> getCachedPhotos() {
        QueryBuilder<PhotoEntity> builder = createQueryBuilder();
        Query<PhotoEntity> cachedQuery = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(false), PhotoEntityDao.Properties.IsCached.eq(true)))
                .build();

        List<PhotoEntity> cachedPhotos = cachedQuery.list();

        List<Photo> photos = new ArrayList<>();
        for (PhotoEntity photoEntity : cachedPhotos) {
            photos.add(_photoEntityTransformer.transform(photoEntity));
        }

        return photos;
    }
}
