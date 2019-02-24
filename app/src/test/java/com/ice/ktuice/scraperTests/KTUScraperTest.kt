package com.ice.ktuice.scraperTests

import com.ice.ktuice.al.services.scraperService.ScraperService
import com.ice.ktuice.al.services.scraperService.exceptions.AuthenticationException
import com.ice.ktuice.al.services.scraperService.ktuScraperService.KTUScraperService
import com.ice.ktuice.impl.FileLoginProvider
import com.ice.ktuice.models.Cookie
import org.jetbrains.anko.getStackTraceString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class KTUScraperTest: KoinTest{

    private val scraperService: ScraperService by inject()

    private val scraperModule: Module = applicationContext {
        provide { KTUScraperService() as ScraperService }
    }

    private lateinit var username: String
    private lateinit var password: String

    /**
     * These tests will only run if there is a file called `ktulogin.local`
     * places at the /app directory (same directory as the /src folder)
     * and for formatting of the file: first line is the username and the second line is the password
     */
    @Before
    fun init(){
        val l = FileLoginProvider.getLoginFromFile()
        username = l.first
        password = l.second
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

    /**
     * Tests if grades are fetched and prints are fetched
     */
    @Test
    fun `Test grade fetching`(){
        val loginModel = scraperService.login(username, password).loginModel!!

        try {
            val grades = scraperService.getAllGrades(loginModel)

            assert(grades.size > 0)
            assert(grades.studentId == loginModel.studentId)
            assert(grades.isUpdating == false)
            assert(!grades.isEmpty())
            assert(grades.yearList.size > 0)
            println("Grades read:")
            for(year in grades.yearList){
                println("\t${year.year.year} (${year.year.id})")
                for(semester in year.semesterList){
                    println("\t\t${semester.semester} (${semester.semester_number})")
                    for(module in semester.moduleList){
                        print("\t\t\t${module.module_name} ")
                        for(grade in module.grades){
                            for(mark in grade.marks){
                                print("$mark ,")
                            }
                        }
                        println()
                    }
                }
            }

        }catch (e : AuthenticationException){
            scraperService.refreshLoginCookies(loginModel)
            `Test grade fetching`()
        }catch (e: Exception){
            println(e.getStackTraceString())
        }
    }

    /**
     * Tests if a request with invalid authentication
     * throws an exception
     */
    @Test
    fun `Unauthenticated login`(){
        val loginModel = scraperService.login(username, password).loginModel!!
        loginModel.authCookies.clear()
        loginModel.authCookies.addAll(
                listOf(
                        /**
                         * Random cookies of the same length and format to become invalid
                         */
                        Cookie("STUDCOOKIE", "759E509D3538D2F9486966714F908BFECC4153325B00897A"),
                        Cookie("_shibsession_64656661756c7468747470733a2f2f756169732e63722e6b74752e6c742f73686962626f6c657468", "_1269e3f9e1ebeeee3d6d9abccc9e2c62")
                )
        )
        var authExceptionThrown = false
        try {
            val grades = scraperService.getAllGrades(loginModel)
        }catch (e : AuthenticationException){
            authExceptionThrown = true
        }
        assert(authExceptionThrown)
    }
}