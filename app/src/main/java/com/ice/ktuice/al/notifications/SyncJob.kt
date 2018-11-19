package com.ice.ktuice.al.notifications

import androidx.work.ListenableWorker
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.YearGradesCollectionModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SyncJob: KoinComponent, AnkoLogger {


    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()
    private val notificationFactory: NotificationFactory by inject()
    private val notificationSummaryGenerator:NotificationSummaryGenerator by inject()

    fun sync(notificationsEnabled: Int){
        info("Starting comparison async")
        //starts the network request
        val dbYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromDB()
        val webYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromWeb()
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
    }
}