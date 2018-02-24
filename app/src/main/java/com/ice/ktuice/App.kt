package com.ice.ktuice

import android.app.Application
import com.ice.ktuice.db.RealmConfig
import com.ice.ktuice.al.koinModules.mainModule
import com.ice.ktuice.al.koinModules.repositoryModule
import io.realm.Realm
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * Created by Andrius on 1/30/2018.
 */
class App: Application() {


    override fun onCreate() {
        super.onCreate()
        RealmConfig.init(this) // initialize the db

        startKoin(this, listOf(mainModule, repositoryModule))

    }
}