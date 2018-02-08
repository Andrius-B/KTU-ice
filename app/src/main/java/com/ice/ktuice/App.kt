package com.ice.ktuice

import android.app.Application
import com.ice.ktuice.DB.RealmConfig
import com.ice.ktuice.AL.koinModules.mainModule
import com.ice.ktuice.AL.koinModules.repositoryModule
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
        val realmModule : Module = applicationContext {
            provide { Realm.getDefaultInstance() as Realm }
        }

        startKoin(this, listOf(mainModule, repositoryModule))

    }
}