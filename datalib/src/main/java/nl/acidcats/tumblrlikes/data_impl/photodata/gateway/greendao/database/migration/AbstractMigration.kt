package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration

import android.database.Cursor
import org.greenrobot.greendao.database.Database

/**
 * Created on 29/10/2018.
 */
abstract class AbstractMigration : Migration {
    protected fun columnExists(db: Database, table: String, colName: String): Boolean {
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("PRAGMA table_info($table)", null)

            cursor?.let {
                val nameColIndex: Int = it.getColumnIndexOrThrow("name")

                while (it.moveToNext()) {
                    val name = it.getString(nameColIndex)
                    if (name == colName) {
                        return true
                    }
                }
            }

            return false
        } finally {
            cursor?.close()
        }
    }
}