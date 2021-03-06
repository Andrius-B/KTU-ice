package com.ice.ktuice

import android.app.Application
import android.content.Context
import com.ice.ktuice.al.koinModules.mainModule
import com.ice.ktuice.al.koinModules.repositoryModule
import com.ice.ktuice.db.RealmConfig
import org.koin.android.ext.android.startKoin

/**
 * Created by Andrius on 1/30/2018.
 * Helper override to start the koin injection
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        RealmConfig.init(this) // initialize the db
        appContext = this
        startKoin(this, listOf(mainModule, repositoryModule))
    }

    companion object {
        private var appContext: Context? = null
        fun getContext(): Context? {
            return appContext
        }
    }
}