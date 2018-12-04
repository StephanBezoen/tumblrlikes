package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao

import android.content.Context
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.DbOpenHelper
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntityTransformer
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.*
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators.LinearPhotoIterator
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators.RandomPhotoIterator
import nl.acidcats.tumblrlikes.db_impl_greendao.DaoMaster
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao
import org.greenrobot.greendao.query.QueryBuilder
import javax.inject.Inject

/**
 * Created on 25/10/2018.
 */
class GreenDAOPhotoDataGatewayImpl @Inject constructor(context: Context) : PhotoDataGateway {

    val DATABASE_NAME = "photos.db"

    private val photoEntityDao: PhotoEntityDao
    private val countQuery by lazy { createQueryBuilder().buildCount() }
    private val uncachedQuery by lazy {
        val builder = createQueryBuilder()
        builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(false), PhotoEntityDao.Properties.IsCached.eq(false)))
                .limit(1)
                .build()
    }
    private val hiddenCachedQuery by lazy {
        val builder = createQueryBuilder()
        builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(true), PhotoEntityDao.Properties.IsCached.eq(true)))
                .build()
    }
    private val filters: MutableMap<FilterType, FilterOption> = mutableMapOf()
    lateinit private var photoIterator: Iterator<PhotoEntity>
    private val photoEntityTransformer = PhotoEntityTransformer()

    init {
        val helper = DbOpenHelper(context, DATABASE_NAME, null)
        photoEntityDao = DaoMaster(helper.writableDatabase).newSession().photoEntityDao;

        filters += FilterType.UNHIDDEN to UnhiddenFilterOptionImpl(photoEntityDao)
        filters += FilterType.FAVORITE to FavoriteFilterOptionImpl(photoEntityDao)
        filters += FilterType.POPULAR to PopularFilterOptionImpl(photoEntityDao)
        filters += FilterType.LATEST to LatestFilterOptionImpl(photoEntityDao)
        filters += FilterType.LEAST_SEEN to LeastSeenFilterOptionImpl(photoEntityDao)
    }

    private fun createQueryBuilder(): QueryBuilder<PhotoEntity> = photoEntityDao.queryBuilder()

    private fun storePhoto(photo: PhotoEntity) = photoEntityDao.save(photo)

    override val photoCount: Long
        get() = countQuery.count()

    override fun getNextPhoto(): Photo? {
        val photoEntity = photoIterator.next()
        photoEntity.viewCount++
        storePhoto(photoEntity)

        return photoEntityTransformer.toPhoto(photoEntity)
    }

    override fun getCachedPhotos(): List<Photo> {
        val builder = createQueryBuilder()
        val query = builder
                .where(builder.and(PhotoEntityDao.Properties.IsHidden.eq(false), PhotoEntityDao.Properties.IsCached.eq(true)))
                .build()

        return photoEntityTransformer.toPhotos(query.list())
    }

    override fun getCachedHiddenPhotos(): List<Photo> = photoEntityTransformer.toPhotos(hiddenCachedQuery.forCurrentThread().list())

    override fun getUncachedPhoto(): Photo? = photoEntityTransformer.toPhoto(uncachedQuery.forCurrentThread().unique())

    override fun getAllPhotos(): List<Photo> = photoEntityTransformer.toPhotos(createQueryBuilder().list())

    override fun hasPhoto(postId: Long): Boolean {
        val count = createQueryBuilder().where(PhotoEntityDao.Properties.PhotoId.eq(postId)).count()
        return count > 0
    }

    override fun storePhotos(photos: List<Photo>) = photoEntityDao.saveInTx(photoEntityTransformer.toPhotoEntity(photos))

    fun getPhotoEntityById(id: Long): PhotoEntity? = createQueryBuilder().where(PhotoEntityDao.Properties.Id.eq(id)).unique()

    override fun getPhotoById(id: Long): Photo? = photoEntityTransformer.toPhoto(getPhotoEntityById(id))

    override fun setPhotoLiked(id: Long, isLiked: Boolean) {
        val photo = getPhotoEntityById(id) ?: return

        if (photo.isLiked != isLiked) {
            photo.isLiked = isLiked
            storePhoto(photo)
        }
    }

    override fun setPhotoFavorite(id: Long, isFavorite: Boolean) {
        val photo = getPhotoEntityById(id) ?: return

        if (photo.isFavorite != isFavorite) {
            photo.isFavorite = isFavorite
            storePhoto(photo)
        }
    }

    override fun setPhotoHidden(id: Long) {
        val photo = getPhotoEntityById(id) ?: return

        if (!photo.isHidden) {
            photo.isHidden = true
            storePhoto(photo)
        }
    }

    override fun setPhotoCached(id: Long, isCached: Boolean, filepath: String?) {
        val photo = getPhotoEntityById(id) ?: return

        photo.isCached = true
        photo.filePath = filepath
        storePhoto(photo)
    }

    override fun setPhotosCached(ids: List<Long>, isCached: Boolean) {
        val totalCount = ids.size
        val maxPerPage = 500
        val pageCount = Math.ceil(totalCount.toDouble() / maxPerPage.toDouble()).toInt()
        val idsPage = ArrayList<Long>()

        for (pageIndex in 0 until pageCount) {
            idsPage.clear()

            val baseIndex = pageIndex * maxPerPage
            val endIndex = Math.min(baseIndex + maxPerPage, totalCount)
            for (idIndex in baseIndex..endIndex) {
                idsPage += ids[idIndex]
            }

            val photoEntities = createQueryBuilder().where(PhotoEntityDao.Properties.Id.`in`(idsPage)).list()
            photoEntities.map { it.isCached = isCached }

            photoEntityDao.saveInTx(photoEntities)
        }
    }

    override fun addPhotoViewTime(id: Long, timeInMs: Long) {
        val photo = getPhotoEntityById(id) ?: return

        photo.viewTime += timeInMs

        storePhoto(photo)
    }

    override fun initFilter(filterType: FilterType) {
        val filterOption = filters[filterType] ?: return

        photoIterator = if (filterType.isRandom) RandomPhotoIterator(filterOption) else LinearPhotoIterator(filterOption)
    }

    override fun hasUncachedPhotos(): Boolean = getUncachedPhoto() != null
}