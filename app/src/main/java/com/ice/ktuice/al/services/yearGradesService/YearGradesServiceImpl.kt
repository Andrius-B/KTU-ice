package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.al.logger.infoFile
import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.al.services.scrapers.base.exceptions.AuthenticationException
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.repositories.yearGradesResponseRepository.YearGradesRepository
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

/**
 * Created by Andrius on 2/24/2018.
 * Access point for getting year grades from both the web and the database
 */
class YearGradesServiceImpl: YearGradesService, KoinComponent, IceLog {

    private val yearGradesRepository: YearGradesRepository by inject()
    private val userService: UserService by inject()
    private val scraperService: ScraperService by inject()
    /**
     * This variable keeps the current state of the service
     */
    private val currentSubject: Subject<YearGradesCollectionModel> = ReplaySubject.create(2)

    private val validator = YearGradesCollectionModelValidator()

    /**
     * Queries the database for a current version, and
     * later returns a new yearGradesCollection from the web
     */
    override fun getYearGradesListSubject(): Subject<YearGradesCollectionModel> {
        val dbGrades = getYearGradesListFromDB()
        yearGradesRepository.setUpdating(dbGrades, true)
        currentSubject.onNext(dbGrades)
        doAsync {
            val webGrades = getYearGradesListFromWeb()
            uiThread {
                persistYearGradesModel(webGrades)
                yearGradesRepository.setUpdating(webGrades, false)
                currentSubject.onNext(webGrades)
            }
        }
        return currentSubject
    }

    /**
     * This function returns an observable state without starting any actual queries
     * or network requests
     */
    override fun getYearGradesListCachedSubject(): Subject<YearGradesCollectionModel>? {
        return currentSubject
    }


    override fun getYearGradesListFromWeb(): YearGradesCollectionModel {
        val login = userService.getLoginForCurrentUser()!!
        val currentGrades = getYearGradesListFromDB()
        yearGradesRepository.setUpdating(currentGrades, true)
        currentSubject.onNext(currentGrades)
        val webGrades: YearGradesCollectionModel
        try {
            webGrades = scraperService.getAllGrades(login)
            webGrades.isUpdating = false
            webGrades.dateUpdated = Date()
            validator.addValidationInformation(webGrades)
            currentSubject.onNext(webGrades)
        }catch (e : AuthenticationException){
            userService.refreshLoginCookies()
            infoFile{ "Fetching grades from web after cookie refresh" }
            return getYearGradesListFromWeb()
        }catch (e: Exception){
            throw e
        }
        infoFile{"YearGradesCollectionModel fetched from the web:"}
        infoFile{"Mark count: ${webGrades.markCnt}"}
        infoFile{"Module count: ${webGrades.moduleCnt}"}
        infoFile{"Semester count: ${webGrades.semesterCnt}"}
        infoFile{"Year count: ${webGrades.yearCnt}"}
        infoFile{"HTML Hash: ${webGrades.htmlHash}"}
        infoFile { "The new table looks like this:" }
        infoFile { webGrades.toString() }
        return webGrades
    }

    override fun getYearGradesListFromDB(): YearGradesCollectionModel {

        val login = userService.getLoginForCurrentUser()!!
        var dbGrades = yearGradesRepository.getByStudCode(login.studentId)

        if(dbGrades == null){
            persistYearGradesModel(YearGradesCollectionModel(login.studentId))
            dbGrades = getYearGradesListFromDB() // create a managed version if none exists
        }else{
            val validationInfo = validator.validateModel(dbGrades)
            if(!validationInfo.valid){
                infoFile{"Object fetched from the database seems to be invalid!"}
                infoFile{"Differences:"}
                infoFile{"Mark count expected: ${dbGrades.markCnt} -> actual ${validationInfo.markCnt}"}
                infoFile{"Module count expected: ${dbGrades.moduleCnt} -> actual ${validationInfo.moduleCnt}"}
                infoFile{"Semester count expected: ${dbGrades.semesterCnt} -> actual ${validationInfo.semesterCnt}"}
                infoFile{"Year count expected: ${dbGrades.yearCnt} -> actual ${validationInfo.yearCnt}"}
                infoFile{"HTML Hash expected: ${dbGrades.htmlHash} -> actual ${validationInfo.htmlHash}"}
            }else{
                infoFile{"YearGradesCollectionModel fetched from database:"}
                infoFile{"Mark count: ${dbGrades.markCnt}"}
                infoFile{"Module count: ${dbGrades.moduleCnt}"}
                infoFile{"Semester count: ${dbGrades.semesterCnt}"}
                infoFile{"Year count: ${dbGrades.yearCnt}"}
                infoFile{"HTML Hash: ${dbGrades.htmlHash}"}
            }
        }
        return dbGrades
    }


    override fun persistYearGradesModel(model: YearGradesCollectionModel){
        info("Persisting year grades model")
        yearGradesRepository.createOrUpdate(model)
        currentSubject.onNext(model)
    }

}