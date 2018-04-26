package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.ktuScraperService.exceptions.AuthenticationException
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/24/2018.
 * Access point for getting year grades from both the web and the database
 */
class YearGradesServiceImpl: YearGradesService, KoinComponent {

    private val yearGradesRepository: YearGradesRepository by inject()
    private val userService: UserService by inject()
    private val scraperService: ScraperService by inject()
    /**
     * This variable keeps the current state of the service
     */
    private val currentSubject: Subject<YearGradesCollectionModel> = ReplaySubject.create(2)


    /**
     * Queries the database for a current version, and
     * later returns a new yearGradesCollection from the web
     */
    override fun getYearGradesList(): Subject<YearGradesCollectionModel> {
        val dbGrades = getYearGradesListFromDB()
        currentSubject.onNext(dbGrades)
        doAsync {
            val webGrades = getYearGradesListFromWeb()
            uiThread {
                persistYearGradesModel(webGrades)
                currentSubject.onNext(webGrades)
            }
        }
        return currentSubject
    }

    /**
     * This function returns an observable state without starting any actual queries
     * or network requests
     */
    override fun getYearGradesListSubject(): Subject<YearGradesCollectionModel>? {
        return currentSubject
    }


    override fun getYearGradesListFromWeb(): YearGradesCollectionModel {
        val login = userService.getLoginForCurrentUser()!!
        val currentGrades = getYearGradesListFromDB()
        yearGradesRepository.setUpdating(currentGrades, true)
        currentSubject.onNext(currentGrades)
        val marks = YearGradesCollectionModel(login.studentId)
        try {
            login.studentSemesters.forEach {
                marks.add(scraperService.getGrades(login, it))
            }
        }catch (e : AuthenticationException){
            userService.refreshLoginCookies()
            return getYearGradesListFromWeb()
        }
        return marks
    }

    override fun getYearGradesListFromDB(): YearGradesCollectionModel {

        val login = userService.getLoginForCurrentUser()!!
        var dbGrades = yearGradesRepository.getByStudCode(login.studentId)
        if(dbGrades == null){
            persistYearGradesModel(YearGradesCollectionModel(login.studentId))
            dbGrades = getYearGradesListFromDB() // create a managed version if none exists
        }
        return dbGrades
    }


    override fun persistYearGradesModel(model: YearGradesCollectionModel){
        yearGradesRepository.createOrUpdate(model)
        currentSubject.onNext(model)
    }

}