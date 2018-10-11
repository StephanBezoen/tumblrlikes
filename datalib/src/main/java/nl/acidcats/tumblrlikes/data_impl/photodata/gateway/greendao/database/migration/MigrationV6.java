package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration;

import android.util.Log;

import org.greenrobot.greendao.database.Database;

import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created on 10/10/2018.
 */
public class MigrationV6 implements Migration {
    private static final String TAG = MigrationV6.class.getSimpleName();

    @Override
    public Integer getVersion() {
        return 6;
    }

    @Override
    public void runMigration(Database db) {
        Log.d(TAG, "runMigration: migrating to V" + getVersion());

        // Added timePerView, updating all existing rows with timePerView = viewTime / viewCount
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.TimePerView.columnName + " INTEGER");
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.TimePerView.columnName + " = " +
                PhotoEntityDao.Properties.ViewTime.columnName + " / " + PhotoEntityDao.Properties.ViewCount.columnName);
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.TimePerView.columnName + " = 0" +
                " WHERE " + PhotoEntityDao.Properties.ViewCount.columnName + " = 0");

        // Added isLiked, updating all existing rows with isLiked = likeCount > 0 ? 1 : 0
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsLiked.columnName + " INTEGER");
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.IsLiked.columnName + " = 1" +
                " WHERE " + PhotoEntityDao.Properties.LikeCount.columnName + " > 0");
        db.execSQL("UPDATE " + PhotoEntityDao.TABLENAME +
                " SET " + PhotoEntityDao.Properties.IsLiked.columnName + " = 0" +
                " WHERE " + PhotoEntityDao.Properties.LikeCount.columnName + " <= 0");
    }
}
