package com.ice.ktuice.scraperTests

import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.KTUScraperService
import com.ice.ktuice.al.services.scrapers.login.LoginService
import com.ice.ktuice.al.services.scrapers.login.LoginServiceImpl
import com.ice.ktuice.al.services.scrapers.timetable.TimetableHandler
import com.ice.ktuice.al.services.scrapers.timetable.TimetableScraper
import com.ice.ktuice.al.services.scrapers.timetable.TimetableScraperHandlerImpl
import com.ice.ktuice.impl.FileLoginProvider
import com.ice.ktuice.models.LoginModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import java.util.*

class TimetableHandlerTest: KoinComponent {

    private val scraperService: ScraperService by inject()
    private val timetableScraper: TimetableScraper by inject()
    
    private val scraperModule: Module = applicationContext {
        provide { KTUScraperService() as ScraperService }
        provide { TimetableScraperHandlerImpl() as TimetableScraper }
        provide { LoginServiceImpl() as LoginService }
    }

    lateinit var username: String
    lateinit var password: String
    lateinit var loginModel: LoginModel

    @Before
    fun init(){
        val l = FileLoginProvider.getLoginFromFile()
        username = l.first
        password = l.second
        startKoin(listOf(scraperModule))
        loginModel = scraperService.login(username, password).loginModel!!
    }
    @After
    fun close(){
        closeKoin()
    }

    @Test
    fun `Fetch tests for next week`(){
        // working with dates pre java8 sucks, but we need
        // to do this in android < 26
        val nowDate = Date()
        val cal = Calendar.getInstance()
        cal.time = nowDate
        cal.add(Calendar.DATE, 0)
        val nextWeekDate = cal.time
        val timetableResponse = timetableScraper.getTimetable(loginModel, listOf(nextWeekDate))
        if(timetableResponse.statusCode != 200){
            println("Something went wrong!")
        }else{
            val timetable = timetableResponse.timetableModel
            println("Current semester: ${timetable.currentSemester.semesterName}")
            println("Current week: ${timetable.currentWeek.weekName}")
            timetable.upcomingTests.forEach { testDateIterator ->
                println("Tests for ${testDateIterator.key.weekName}:")
                testDateIterator.value.forEach {
                    println("\t $it")
                }
            }
            if(timetable.upcomingTests.isEmpty()){
                println("No tests found for next week")
            }
        }

        assert(timetableResponse.statusCode == 200)
    }
}