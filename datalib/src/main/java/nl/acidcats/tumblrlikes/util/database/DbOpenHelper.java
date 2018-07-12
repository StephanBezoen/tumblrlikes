package nl.acidcats.tumblrlikes.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.acidcats.tumblrlikes.data.repo.photo.store.entity.DaoMaster;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.util.database.migration.Migration;
import nl.acidcats.tumblrlikes.util.database.migration.MigrationV5;

/**
 * Created by stephan on 18/04/2017.
 */

public class DbOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = DbOpenHelper.class.getSimpleName();

    private final Context _context;
    private final boolean _debug = BuildConfig.DEBUG;


    public DbOpenHelper(Context context, String name) {
        super(context, name);

        _context = context;
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);

        _context = context;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (_debug) Log.d(TAG, "onUpgrade: from " + oldVersion + " to " + newVersion);

        for (Migration migration : getMigrations()) {
            if (migration.getVersion() <= newVersion) {
                migration.runMigration(db);
            }
        }

//        LocalBroadcastManager.getInstance(_context).sendBroadcast(new Intent(Broadcasts.DATABASE_RESET));
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new MigrationV5());

        // Sorting just to be safe, in case other people add migrations in the wrong order.
        Collections.sort(migrations, (m1, m2) -> m1.getVersion().compareTo(m2.getVersion()));

        return migrations;
    }

}
