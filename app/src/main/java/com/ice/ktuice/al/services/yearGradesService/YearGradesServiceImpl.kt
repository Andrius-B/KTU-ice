package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.R
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.al.services.scrapers.base.exceptions.AuthenticationException
import com.ice.ktuice.al.services.scrapers.base.exceptions.ValidationException
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.repositories.yearGradesResponseRepository.YearGradesRepository
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private val preferenceRepository: PreferenceRepository by inject()
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
        GlobalScope.launch(Dispatchers.Default) {
            val webGrades = getYearGradesListFromWeb()
            launch(Dispatchers.Main) {
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
            val refreshRetries = preferenceRepository.getValue(R.string.grade_scraper_retries).toIntOrNull() ?: 0
            preferenceRepository.setValue(R.string.grade_scraper_retries, (refreshRetries + 1).toString())

            if(refreshRetries >= 10){
                info { "Crash due to repeatedly failing authentication" }
                throw AuthenticationException("Authentication failed after $refreshRetries requests!", e)
            }

            return getYearGradesListFromWeb()
        }catch (e: Exception){
            throw e
        }
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
                info{"Database object validation failed!"}
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