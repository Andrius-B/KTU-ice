package com.ice.ktuice.al.koinModules

import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.SharedPreferenceRepositoryImpl
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactoryImpl
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.notifications.NotificationSummaryGenerator
import com.ice.ktuice.al.notifications.NotificationSummaryGeneratorImpl
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.userService.UserServiceImpl
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.al.services.yearGradesService.YearGradesServiceImpl
import com.ice.ktuice.scraperService.ktuScraperService.KTUScraperService
import com.ice.ktuice.scraperService.ScraperService
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * Created by Andrius on 1/31/2018.
 * All the main dependencies are declared here
 */
val mainModule: Module = applicationContext {
    provide { SharedPreferenceRepositoryImpl(this.androidApplication()) as PreferenceRepository }
    provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
    provide { YearGradesServiceImpl() as YearGradesService }
    provide { UserServiceImpl() as UserService }
    provide { NotificationFactoryImpl(this.androidApplication()) as NotificationFactory }
    provide { NotificationSummaryGeneratorImpl(this.androidApplication()) as NotificationSummaryGenerator }
    provide { KTUScraperService() as ScraperService }
}