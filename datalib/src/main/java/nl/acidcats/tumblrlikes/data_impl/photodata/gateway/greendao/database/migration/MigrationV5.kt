package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration

import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao
import org.greenrobot.greendao.database.Database

/**
 * Created on 25/10/2018.
 */
class MigrationV5 : Migration {
    override val version: Int = 5

    override fun runMigration(db: Database) {
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsFavorite.columnName + " INTEGER")
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsHidden.columnName + " INTEGER")
    }
}