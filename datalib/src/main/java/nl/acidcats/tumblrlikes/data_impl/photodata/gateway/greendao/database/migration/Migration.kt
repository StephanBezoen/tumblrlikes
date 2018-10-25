package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.database.migration

import org.greenrobot.greendao.database.Database

/**
 * Created by stephan on 11/05/2017.
 */
interface Migration {
    val version: Int

    fun runMigration(db: Database)
}
