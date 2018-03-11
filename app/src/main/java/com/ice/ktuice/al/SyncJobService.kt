package com.ice.ktuice.al

import android.app.job.JobParameters
import android.app.job.JobService
import com.ice.ktuice.al.GradeTable.GradeTableManager
import com.ice.ktuice.al.GradeTable.NotificationFactory
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.YearGradesCollectionModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 */
class SyncJobService: JobService(), KoinComponent {
    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()

    private var jobParams: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        println("onStartJob testing out the jobScheduler API")
        jobParams = params
        doAsync({
            println(it.getStackTraceString())
            jobFinished(jobParams, false)
        },{
            println("Getting logged in user on the service!")
            //starts the network request
            val dbYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromDB()
            val webYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromWeb()
            if(dbYear != null && webYear != null){

                val totalDifference = mutableListOf<Difference>()

                webYear.forEach {
                    val freshYear = it
                    val previousYear = dbYear.find { it.year.equals(freshYear.year) }
                    if(previousYear != null) {
                        totalDifference.addAll(yearGradesComparator.compare(previousYear, freshYear))
                    }
                }

                uiThread {
                    println("Differences found:" + totalDifference.size)
                    if(totalDifference.isNotEmpty()){
                        println("Differences found:"+totalDifference.size)
                        totalDifference.forEach{
                            println(String.format("\t\t type:%s change:%s", it.field.toString(), it.change.toString()))
                        }
                        NotificationFactory(applicationContext).pushNotification(generateDifSummary(totalDifference))
                    }
                    println("Persisting the year list to the database, from the service!")
                    yearGradesService.persistYearGradesModel(webYear)
                }
                println("service finished without errors!")
                jobFinished(jobParams, false)
            }
        })
        return true
    }

    private fun generateDifSummary(diff: List<Difference>): String{
        var marksAdded = 0
        diff.forEach {
            if(it.field == Difference.Field.Grade && it.change == Difference.FieldChange.Added){
                marksAdded++
            }
        }
        return String.format("New marks found:%s", marksAdded)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("onStopJob testing out the jobScheduler API")
        return false
    }

}