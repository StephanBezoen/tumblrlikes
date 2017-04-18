package nl.acidcats.tumblrlikes.util.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.constants.Broadcasts;
import nl.acidcats.tumblrlikes.data.vo.db.DaoMaster;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;

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

        PhotoEntityDao.dropTable(db, true);

        onCreate(db);

        LocalBroadcastManager.getInstance(_context).sendBroadcast(new Intent(Broadcasts.DATABASE_RESET));
    }
}
