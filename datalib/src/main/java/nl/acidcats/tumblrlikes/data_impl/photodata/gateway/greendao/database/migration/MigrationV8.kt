package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration

import android.database.Cursor
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao
import org.greenrobot.greendao.database.Database

/**
 * Created on 29/10/2018.
 */
class MigrationV8: AbstractMigration() {
    override val version: Int = 8

    override fun runMigration(db: Database) {
        if (!columnExists(db, PhotoEntityDao.TABLENAME, PhotoEntityDao.Properties.LikeCount.columnName)) {
            db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.LikeCount.columnName + " INTEGER")
        }
    }
}