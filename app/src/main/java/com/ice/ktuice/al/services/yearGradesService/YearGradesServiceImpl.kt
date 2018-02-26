package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/24/2018.
 * Access point for getting year grades from both the web and the database
 */
class YearGradesServiceImpl: YearGradesService, KoinComponent {

    private val yearGradesRepository: YearGradesRepository by inject()
    private val userService: UserService by inject()

    override fun getYearGradesListFromWeb(): YearGradesCollectionModel {
        val login = userService.getLoginForCurrentUser()!!
        val marks = YearGradesCollectionModel(login.studentId)
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

    override fun getYearGradesListFromDB(async: Boolean): YearGradesCollectionModel {
        println("Reading year grade models from the database!")
        val login = userService.getLoginForCurrentUser()!!
        var dbGrades = yearGradesRepository.getByStudCode(login.studentId, async)
        if(dbGrades == null){
            persistYearGradesModel(YearGradesCollectionModel(login.studentId))
            dbGrades = getYearGradesListFromDB(false) // create a managed version if none exists
        }
        return dbGrades
    }


    override fun persistYearGradesModel(model: YearGradesCollectionModel){
        yearGradesRepository.createOrUpdate(model)
    }

}