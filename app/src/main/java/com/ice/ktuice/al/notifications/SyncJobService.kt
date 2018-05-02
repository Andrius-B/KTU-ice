package com.ice.ktuice.al.notifications

import android.app.job.JobParameters
import android.app.job.JobService
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.YearGradesCollectionModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 */
class SyncJobService: JobService(), KoinComponent, AnkoLogger {

    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()
    private val notificationFactory: NotificationFactory by inject()
    private val notificationSummaryGenerator:NotificationSummaryGenerator by inject()

    private var jobParams: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        jobParams = params
        doAsync({
            info(it.getStackTraceString())
            jobFinished(jobParams, true)
        },{
            info("Starting comparison async")
            //starts the network request
            val dbYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromDB()
            val webYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromWeb()
            val notificationsEnabled = params?.extras?.getInt(applicationContext.resources.getString(R.string.notification_enabled_flag)) ?: 1
            //notifications are enabled if not specified otherwise in the extras!
            if(dbYear != null && webYear != null && notificationsEnabled > 0){
                val totalDifference = mutableListOf<Difference>()

                webYear.yearList.forEach {
                    val freshYear = it
                    val previousYear = dbYear.find { it.year.equals(freshYear.year) }
                    if(previousYear != null) {
                        val newDiff = yearGradesComparator.compare(previousYear, freshYear)
                        totalDifference.addAll(newDiff)
                    }
                }


                if(totalDifference.isNotEmpty()){
                    info("Differences found: ${totalDifference.size}")
                    totalDifference.forEach{
                        info(String.format("\t\t type:%s change:%s", it.field.toString(), it.change.toString()))
                    }
                    try {
                        val message = notificationSummaryGenerator.generateSummaryFromDifferences(totalDifference)
                        notificationFactory.pushNotification(message)
                        info("Notification pushed!")
                    }catch (e: Exception){
                        info(e.getStackTraceString())
                        info("Notification push failed!")
                    }
                }
            }
            webYear!!.isUpdating = false
            yearGradesService.persistYearGradesModel(webYear)
            info("service finished without errors!")
            jobFinished(jobParams, false)
        })
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

}