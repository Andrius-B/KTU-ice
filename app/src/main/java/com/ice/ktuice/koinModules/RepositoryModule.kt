package com.ice.ktuice.koinModules

import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepositoryImpl
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val repositoryModule: Module = applicationContext {
    provide { LoginRepositoryImpl() as LoginRepository }
}