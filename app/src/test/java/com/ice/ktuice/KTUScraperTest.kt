package com.ice.ktuice

import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.notifications.NotificationSummaryGenerator
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.ktuScraperService.KTUScraperService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito
import java.io.File

class KTUScraperTest: KoinTest{

    private val scraperService: ScraperService by inject()

    private val scraperModule: Module = applicationContext {
        provide { KTUScraperService() as ScraperService }
    }

    lateinit var username: String
    lateinit var password: String

    /**
     * These tests will only run if there is a file called `ktulogin.local`
     * places at the /app directory (same directory as the /src folder)
     * and for formatting of the file: first line is the username and the second line is the password
     */
    @Before
    fun init(){
        val inputStream = File("ktulogin.local").inputStream()
        inputStream.bufferedReader().use {
            val lines = it.readLines()
            username = lines[0]
            password = lines[1]
        }
        startKoin(listOf(scraperModule))
    }
    @After
    fun cleanup(){
        closeKoin()
    }

    /**
     * Tests the login if it returns any cookies and also prints the cookies out for preview
     */
    @Test
    fun `Login Test`(){
        val loginResponse = scraperService.login(username, password)

        val loginModel = loginResponse.loginModel!!
        val loginResponseCode = loginResponse.statusCode

        assert(loginResponseCode == 200)
        assert(!loginModel.getCookieMap().isEmpty())
        assert(loginModel.studentSemesters.size > 0)

        println("Semesters:")
        for(semester in loginModel.studentSemesters){
            println("${semester.id}, ${semester.year}")
        }

        println("Cookies fetched:")
        for(item in loginModel.authCookies){
            println("${item.key} => ${item.content}")
        }
    }

    @Test
    fun `Test grade fetching`(){
        val loginModel = scraperService.login(username, password).loginModel!!

        val grades = scraperService.getGrades(loginModel, loginModel.studentSemesters[0]!!)
    }
}