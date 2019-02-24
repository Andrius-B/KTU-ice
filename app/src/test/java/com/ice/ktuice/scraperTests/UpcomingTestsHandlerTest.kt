package com.ice.ktuice.scraperTests

import com.ice.ktuice.al.services.scraperService.ScraperService
import com.ice.ktuice.al.services.scraperService.ktuScraperService.KTUScraperService
import com.ice.ktuice.al.services.scraperService.ktuScraperService.handlers.UpcomingTestsHandler
import com.ice.ktuice.impl.FileLoginProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject

class UpcomingTestsHandlerTest: KoinComponent {

    private val scraperService: ScraperService by inject()
    private val scraperModule: Module = applicationContext {
        provide { KTUScraperService() as ScraperService }
    }

    lateinit var username: String
    lateinit var password: String

    @Before
    fun init(){
        val l = FileLoginProvider.getLoginFromFile()
        username = l.first
        password = l.second
        startKoin(listOf(scraperModule))
    }
    @After
    fun close(){
        closeKoin()
    }

//    @Test
//    fun `Fetch upcomming tests`(){
//        val loginModel = scraperService.login(username, password).loginModel!!
//
//        UpcomingTestsHandler.getUpcoming(loginModel)
//    }

    @Test
    fun `Fetch initial calendar`(){
        val loginModel = scraperService.login(username, password).loginModel!!

        UpcomingTestsHandler.getSemesterBeginings(loginModel)
    }

}