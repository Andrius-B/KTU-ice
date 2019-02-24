package com.ice.ktuice.al.notifications

import com.ice.ktuice.al.gradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.al.logger.infoFile
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.al.settings.AppSettings
import com.ice.ktuice.models.GradeModel
import com.ice.ktuice.models.YearGradesCollectionModel
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SyncJob: KoinComponent, IceLog {

    private val settings: AppSettings by inject()
    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()
    private val notificationFactory: NotificationFactory by inject()
    private val notificationSummaryGenerator:NotificationSummaryGenerator by inject()

    fun sync(notificationsEnabled: Int){
        infoFile("Starting comparison async")
        //starts the network request
        val dbYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromDB()
        if(!settings.networkingEnabled) return // break out before syncing if networking is disabled
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
                infoFile("Differences found: ${totalDifference.size}")
                totalDifference.forEach{
                    val grade = it.supplementary as GradeModel?
                    infoFile("\t\t type: ${it.field} change:${it.change} of grade ${grade?.marks?.toArray()?.joinToString(", ") ?: "null"}")
                }
                try {
                    val message = notificationSummaryGenerator.generateSummaryFromDifferences(totalDifference)
                    notificationFactory.pushNotification(message)
                    infoFile("====================================")
                    infoFile("Notification pushed!")
                    infoFile("====================================")
                }catch (e: Exception){
                    infoFile(e.getStackTraceString())
                    infoFile("Notification push failed!")
                }
            }
        }
        webYear!!.isUpdating = false
        yearGradesService.persistYearGradesModel(webYear)
        infoFile("service finished without errors!")
    }
}