package com.ice.ktuice.al.koinModules

import com.ice.ktuice.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.repositories.prefrenceRepository.SharedPreferenceRepositoryImpl
import com.ice.ktuice.al.gradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.gradeTable.notifications.NotificationFactoryImpl
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.notifications.NotificationSummaryGenerator
import com.ice.ktuice.al.notifications.NotificationSummaryGeneratorImpl
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.userService.UserServiceImpl
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.al.services.yearGradesService.YearGradesServiceImpl
import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.KTUScraperService
import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.al.services.scrapers.calendar.CalendarScraper
import com.ice.ktuice.al.services.scrapers.calendar.CalendarScraperHanderImpl
import com.ice.ktuice.al.services.scrapers.login.LoginService
import com.ice.ktuice.al.services.scrapers.login.LoginServiceImpl
import com.ice.ktuice.al.services.scrapers.timetable.TimetableScraper
import com.ice.ktuice.al.services.scrapers.timetable.TimetableScraperHandlerImpl
import com.ice.ktuice.al.settings.AppSettings
import com.ice.ktuice.al.settings.AppSettingsPreferencesImpl
import com.ice.ktuice.viewModels.gradesFragment.GradesFragmentViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Created by Andrius on 1/31/2018.
 * All the main dependencies are declared here
 */
val mainModule = module {
    single<PreferenceRepository> { SharedPreferenceRepositoryImpl(this.androidApplication()) }
    single<YearGradesModelComparator> { YearGradesModelComparatorImpl() }
    single<YearGradesService> { YearGradesServiceImpl() }
    single<UserService> { UserServiceImpl() }
    single<NotificationFactory> { NotificationFactoryImpl(this.androidApplication()) }
    single<NotificationSummaryGenerator> { NotificationSummaryGeneratorImpl(this.androidApplication()) }
    single { GradesFragmentViewModel() }
    single<ScraperService> { KTUScraperService() }
    single<AppSettings> { AppSettingsPreferencesImpl(this.androidApplication()) }
    single<CalendarScraper> { CalendarScraperHanderImpl() }
    single<TimetableScraper> { TimetableScraperHandlerImpl() }
    single<LoginService> { LoginServiceImpl() }
}