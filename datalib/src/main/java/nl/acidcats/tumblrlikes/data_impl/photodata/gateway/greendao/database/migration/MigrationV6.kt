package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration

import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao
import org.greenrobot.greendao.database.Database

/**
 * Created on 25/10/2018.
 */
class MigrationV6: Migration {
    override val version: Int = 6

    override fun runMigration(db: Database) {
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.TimePerView.columnName + " INTEGER")
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.TimePerView.columnName + " = " +
                PhotoEntityDao.Properties.ViewTime.columnName + " / " + PhotoEntityDao.Properties.ViewCount.columnName)
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.TimePerView.columnName + " = 0" +
                " WHERE " + PhotoEntityDao.Properties.ViewCount.columnName + " = 0")

        // Added isLiked, updating all existing rows with isLiked = likeCount > 0 ? 1 : 0
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsLiked.columnName + " INTEGER")
    }
}
