package com.ice.ktuice.al.koinModules

import com.ice.ktuice.repositories.calendarRepository.CalendarRepository
import com.ice.ktuice.repositories.calendarRepository.CalendarRepositoryImpl
import com.ice.ktuice.repositories.yearGradesResponseRepository.YearGradesRepository
import com.ice.ktuice.repositories.yearGradesResponseRepository.YearGradesRepositoryImpl
import com.ice.ktuice.repositories.loginRepository.LoginRepository
import com.ice.ktuice.repositories.loginRepository.LoginRepositoryImpl
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val repositoryModule: Module = applicationContext {
    provide { LoginRepositoryImpl() as LoginRepository }
    provide { YearGradesRepositoryImpl() as YearGradesRepository }
    provide { CalendarRepositoryImpl() as CalendarRepository }
}