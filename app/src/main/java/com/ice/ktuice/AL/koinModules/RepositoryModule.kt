package com.ice.ktuice.AL.koinModules

import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepositoryImpl
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepositoryImpl
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val repositoryModule: Module = applicationContext {
    provide { LoginRepositoryImpl() as LoginRepository }
    provide { YearGradesRepositoryImpl() as YearGradesRepository }
}