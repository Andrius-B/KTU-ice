package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/24/2018.
 */
class YearGradesServiceImpl: YearGradesService, KoinComponent {

    private val yearGradesRepository: YearGradesRepository by inject()
    private val userService: UserService by inject()

    override fun getYearGradesListFromWeb(): List<YearGradesModel> {
        val login = userService.getLoginForCurrentUser()!!
        val marks = mutableListOf<YearGradesModel>()
        try {
            login.studentSemesters.forEach {
                marks.add(ScraperService.getGrades(login, it))
            }
        }catch (e : AuthenticationException){
            userService.refreshLoginCookies()
            return getYearGradesListFromWeb()
        }
        return marks
    }

    override fun getYearGradesListFromDB(): RealmResults<YearGradesModel> {
        println("Reading year grade models from the database!")
        val login = userService.getLoginForCurrentUser()!!
        val dbGrades = yearGradesRepository.getByStudCode(login.studentId)
//        dbGrades.forEach {
//            println(String.format("YearGradesModel found: date stamp at %s, of year %s with semesters %s and hash %s",
//                                  it.dateStamp.toString(), it.year.year, it.semesterList.size, it.hashCode.toString()))
//        }
        return dbGrades
    }


    override fun persistYearGradeModels(modelList: List<YearGradesModel>){
        modelList.forEach{ persistYearGradeModel(it) }
    }

    override fun persistYearGradeModel(model: YearGradesModel){
        yearGradesRepository.createOrUpdate(model)
    }
}