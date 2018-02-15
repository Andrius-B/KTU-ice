package com.ice.ktuice.AL.GradeTable

import com.ice.ktuice.AL.GradeTable.GradeTableModels.GradeTableFactory
import com.ice.ktuice.AL.GradeTable.GradeTableModels.GradeTableModel
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.YearGradesModel
import com.ice.ktuice.scraper.scraperService.Exceptions.AuthenticationException
import com.ice.ktuice.scraper.scraperService.ScraperService
import io.realm.Realm
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.Future

/**
 * Created by Andrius on 2/15/2018.
 * A helper class to contain the logic of the grade table and supply the view models
 * TODO refactor and add database usage
 */
class GradeTableManager: KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()
    //private val gradeRepository: GradeResponseRepository by inject()
    private val loginRepository: LoginRepository by inject()

    fun getGradeTableModel(): GradeTableModel{
        val oldLogin = getLoginForCurrentUser()
        val login = refreshLoginCookies(oldLogin)
        val table = constructGradeTableModel(login)
        return table!!
    }

    fun getLoginForCurrentUser(): LoginModel {
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if (requestedStudentId.isBlank()) {
            println("StudentCode not found, quitting!")
            throw NullPointerException("Student code is not found, can not initialize the grade table component!")
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId)
        if (loginModel == null) {
            println("Login model is null!")
            throw NullPointerException("Login model for the requested code is null, can not initialize the grade table component")
        }else{
            return loginModel
        }
    }

    fun constructGradeTableModel(login: LoginModel, yearGradesList: List<YearGradesModel>? = null): GradeTableModel?{
        try{
            val loginModel = refreshLoginCookies(login)
            val marks = yearGradesList ?: getYearGradesList(loginModel)
            val table = GradeTableFactory.buildGradeTableFromYearGradesModel(marks)
            /*println("Printing the grade table!")
            println("Table:" + table.toString())
            println("Seen weeks:" + table.getWeekListString())
            table.printRowCounts()*/
            return table
        }catch (it: Exception){
            when(it.javaClass){
                AuthenticationException::class.java -> {
                    try {
                        println("refreshing login cookies!")
                        val newLoginModel = refreshLoginCookies(login)
                        println("login cookies refreshed, should initialize grade table")
                        //TODO cookie refreshing
                        return constructGradeTableModel(newLoginModel)
                        //println("grade table initialized!")
                    }catch (e: Exception){
                        println(e.getStackTraceString())
                    }
                }
            }
            println(it.getStackTraceString())
        }
        return null
    }

    fun getYearGradesList(loginModel: LoginModel): List<YearGradesModel> {
        val marks = mutableListOf<YearGradesModel>()
        loginModel.studentSemesters.forEach {
            marks.add(ScraperService.getGrades(loginModel, it))
        }
        return marks
    }

    private fun refreshLoginCookies(loginModel: LoginModel): LoginModel {
        val newLoginModelResponse = ScraperService.login(loginModel.username, loginModel.password)
        val newLoginModel = newLoginModelResponse.loginModel!!
        return newLoginModel
    }

}