package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration.Migration
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration.MigrationV5
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration.MigrationV6
import nl.acidcats.tumblrlikes.db_impl_greendao.DaoMaster
import org.greenrobot.greendao.database.Database

/**
 * Created on 25/10/2018.
 */
class DbOpenHelper constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?) : DaoMaster.OpenHelper(context, name, factory) {
    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        if (db == null) return

        for (migration in getMigrations()) {
            if (migration.version in (oldVersion + 1)..newVersion) {
                migration.runMigration(db)
            }
        }
    }

    private fun getMigrations(): List<Migration> {
        val migrations = ArrayList<Migration>()
        migrations += MigrationV5()
        migrations += MigrationV6()

        migrations.sortBy { it.version }

        return migrations
    }
}