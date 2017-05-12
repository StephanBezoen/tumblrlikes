package nl.acidcats.tumblrlikes.util.database.migration;

import org.greenrobot.greendao.database.Database;

/**
 * Created by stephan on 11/05/2017.
 */
public interface Migration {
    Integer getVersion();

    void runMigration(Database db);
}
