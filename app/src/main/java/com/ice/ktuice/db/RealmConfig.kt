package com.ice.ktuice.db

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Andrius on 1/24/2018.
 */
object RealmConfig {
    fun init(context: Context) {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .name("database.realm")
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)
        Realm.getInstance(config)
    }
}