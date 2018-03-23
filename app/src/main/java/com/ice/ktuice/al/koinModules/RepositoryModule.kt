package com.ice.ktuice.al.koinModules

import com.ice.ktuice.DAL.repositories.calendarRepository.CalendarRepository
import com.ice.ktuice.DAL.repositories.calendarRepository.CalendarRepositoryImpl
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepositoryImpl
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepositoryImpl
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val repositoryModule: Module = applicationContext {
    provide { LoginRepositoryImpl() as LoginRepository }
    provide { YearGradesRepositoryImpl() as YearGradesRepository }
    provide { CalendarRepositoryImpl() as CalendarRepository }
}