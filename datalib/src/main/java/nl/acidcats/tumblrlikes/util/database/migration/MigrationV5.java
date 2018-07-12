package nl.acidcats.tumblrlikes.util.database.migration;

import org.greenrobot.greendao.database.Database;

import nl.acidcats.tumblrlikes.data.repo.photo.store.entity.PhotoEntityDao;

/**
 * Created by stephan on 11/05/2017.
 */

public class MigrationV5 implements Migration {
    @Override
    public Integer getVersion() {
        return 5;
    }

    @Override
    public void runMigration(Database db) {

        /*
        * Added to PhotoEntity:
        *     private boolean isFavorite;

    private boolean isHidden;

    private int likeCount;

    private String hash;

        *
        * */

        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsFavorite.columnName + " INTEGER");
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.IsHidden.columnName + " INTEGER");
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.LikeCount.columnName + " INTEGER");
        db.execSQL("ALTER TABLE " + PhotoEntityDao.TABLENAME + " ADD COLUMN " + PhotoEntityDao.Properties.Hash.columnName + " TEXT");
    }
}
