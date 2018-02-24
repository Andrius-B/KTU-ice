package com.ice.ktuice.al.GradeTable

import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableFactory
import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableModel
import com.ice.ktuice.al.GradeTable.gradeTableModels.SemesterAdapterItem
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.ScraperService
import io.realm.RealmResults
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/15/2018.
 * A helper class to contain the logic of the grade table and supply the view models
 * TODO refactor
 */
class GradeTableManager: KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()
    private val yearGradesRepository: YearGradesRepository by inject()
    private val loginRepository: LoginRepository by inject()

    fun getGradeTableModel(): GradeTableModel{
        val oldLogin = getLoginForCurrentUser()
        val login = refreshLoginCookies(oldLogin)

        val yearList = getYearGradesListFromWeb(login)
        val table = constructGradeTableModel(login, yearList)
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

    fun constructGradeTableModel(login: LoginModel, yearGradesList: List<YearGradesModel>): GradeTableModel?{
        try{

            val table = GradeTableFactory.buildGradeTableFromYearGradesModel(yearGradesList)
            println("Printing the grade table!")
            println("Table:" + table.toString())
            println("Seen weeks:" + table.getWeekListString())
            table.printRowCounts()
            return table
        }catch (it: Exception){
            when(it.javaClass){
                AuthenticationException::class.java -> {
                    try {
                        println("refreshing login cookies!")
                        val newLoginModel = refreshLoginCookies(login)
                        println("login cookies refreshed, should initialize grade table")
                        return constructGradeTableModel(newLoginModel, yearGradesList)
                    }catch (e: Exception){
                        println(e.getStackTraceString())
                    }
                }
            }
            println(it.getStackTraceString())
        }
        return null
    }


    fun constructSemesterAdapterSpinnerItemList(yearsList: List<YearGradesModel>):List<SemesterAdapterItem>{
        val itemList = mutableListOf<SemesterAdapterItem>()
        yearsList.forEach {
            val year = it.year
            it.semesterList.forEach {
                // deep copy to avoid realm thread issues
                itemList.add(SemesterAdapterItem(it.semester, it.semester_number, YearModel(year.id, year.year)))
            }
        }
        return itemList
    }


    fun getYearGradesListFromWeb(login: LoginModel): List<YearGradesModel> {
        val marks = mutableListOf<YearGradesModel>()
        try {
            login.studentSemesters.forEach {
                marks.add(ScraperService.getGrades(login, it))
            }
        }catch (e :AuthenticationException){
            val refreshedLogin = refreshLoginCookies(login)
            return getYearGradesListFromWeb(refreshedLogin)
        }
        return marks
    }

    fun getYearGradesListFromDB(login: LoginModel): RealmResults<YearGradesModel> {
        println("Reading year grade models from the database!")
        val dbGrades = yearGradesRepository.getByStudCode(login.studentId)
//        dbGrades.forEach {
//            println(String.format("YearGradesModel found: date stamp at %s, of year %s with semesters %s and hash %s",
//                                  it.dateStamp.toString(), it.year.year, it.semesterList.size, it.hashCode.toString()))
//        }
        return dbGrades
    }

    private fun refreshLoginCookies(loginModel: LoginModel): LoginModel {
        val newLoginModelResponse = ScraperService.login(loginModel.username, loginModel.password)
        val newLoginModel = newLoginModelResponse.loginModel!!
        return newLoginModel
    }

    fun persistYearGradeModels(modelList: List<YearGradesModel>){
        modelList.forEach{ persistYearGradeModel(it) }
    }

    fun persistYearGradeModel(model: YearGradesModel){
        yearGradesRepository.createOrUpdate(model)
    }

}