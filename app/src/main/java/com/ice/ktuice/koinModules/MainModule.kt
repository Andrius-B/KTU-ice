package com.ice.ktuice.koinModules

import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.SharedPreferenceRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * Created by Andrius on 1/31/2018.
 */
val mainModule: Module = applicationContext {
    provide { SharedPreferenceRepositoryImpl(this.androidApplication()) as PreferenceRepository }
}