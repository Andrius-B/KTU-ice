package com.ice.ktuice.AL

import android.app.job.JobParameters
import android.app.job.JobService
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.YearModel
import com.ice.ktuice.scraper.scraperService.Exceptions.AuthenticationException
import com.ice.ktuice.scraper.scraperService.ScraperService
import io.realm.Realm
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 */
class SyncJobService: JobService(), KoinComponent {

    private val preferenceRepository: PreferenceRepository by inject()
    private val loginRepository: LoginRepository by inject()
    private var jobParams: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        println("onStartJob testing out the jobScheduler API")
        jobParams = params
        doAsync({
            println(it.getStackTraceString())
            jobFinished(jobParams, false)
        },{
            println("Getting logged in user on the service!")
            val login = getLoggedInUser()
            println("gettring grade table on the service!")
            fetchGradeTable(login!!, login.studentSemesters[0]!!)
            println("service finished without errors!")
            jobFinished(jobParams, false)
        })
        return false
    }

    private fun getLoggedInUser(): LoginModel? {
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            //throw NullPointerException("Student code is not found, can not initialize the grade table component!")
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId)
        if(loginModel == null){
            println("Login model is null!")
            //throw NullPointerException("Login model for the requested code is null, can not initialize the grade table component")
        }
        return loginModel
    }

    private fun fetchGradeTable(loginModel: LoginModel, yearModel: YearModel){
        doAsync(
                {
                    when(it.javaClass){
                        AuthenticationException::class.java -> {
                            try {
                                println("refreshing login cookies!")
                                val newLoginModel = refreshLoginCookies(loginModel)
                                println("login cookies refreshed, initializing grade table")
                                fetchGradeTable(newLoginModel, yearModel)
                                println("grade table initialized!")
                            }catch (e: Exception){
                                println(e.getStackTraceString())
                            }
                        }
                    }
                },
                {

//                    val marks = ScraperService.getGrades(loginModel, yearModel)
//                    Log.d("INFO", String.format("GradeResponseModel code:"+marks.statusCode))
//
//                    val table = GradeTableFactory.buildGradeTableFromMarkResponse(marks)
//                    println("Printing the grade table!")
//                    //println("Table:" + table.toString())
//                    println("Seen weeks:" + table.getWeekListString())
//                    table.printRowCounts()
//                    uiThread ({
//                        gradeRepository.createOrUpdate(marks, YearGradesMetadataModel(loginModel.studentId, yearModel, Date()), Realm.getDefaultInstance())
//                    })
                })
    }

    private fun refreshLoginCookies(loginModel: LoginModel): LoginModel {
        println(String.format("login cookies username:%s ,pw:%s",loginModel.username, loginModel.password))
        val newLoginModelResponse = ScraperService.login(loginModel.username, loginModel.password)
        println("refreshing login cookies response:"+newLoginModelResponse.statusCode)
        val newLoginModel = newLoginModelResponse.loginModel!!
        loginRepository.createOrUpdate(newLoginModel)
        return newLoginModel
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("onStopJob testing out the jobScheduler API")
        return false
    }

}