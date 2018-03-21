package com.ice.ktuice.al

import android.app.job.JobParameters
import android.app.job.JobService
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.notifications.NotificationFactoryImpl
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.GradeModel
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
    /**
     * the R.string.notification_new_mark_found is a formated string with a placeholder for a string (which should contain a mark, that was found)
     */
    private val newMarkString = applicationContext.getString(R.string.notification_new_mark_found)
    private val newMarksString = applicationContext.getString(R.string.notification_new_marks_found)
    private val markChangedString = applicationContext.getString(R.string.notification_mark_updated)
    private val gradeTableChanged = applicationContext.getString(R.string.notification_grade_table_changed)

    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()
    private val notificationFactory: NotificationFactory by inject()

    private var jobParams: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        println("onStartJob testing out the jobScheduler API")
        jobParams = params
        doAsync({
            println(it.getStackTraceString())
            jobFinished(jobParams, false)
        },{
            println("Starting comparison async")
            //starts the network request
            val dbYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromDB()
            val webYear: YearGradesCollectionModel? = yearGradesService.getYearGradesListFromWeb()
            if(dbYear != null && webYear != null){
                println("Both the models are not null")
                val totalDifference = mutableListOf<Difference>()

                webYear.yearList.forEach {
                    val freshYear = it
                    val previousYear = dbYear.find { it.year.equals(freshYear.year) }
                    if(previousYear != null) {
                        println("previous year found!")
                        val newDiff = yearGradesComparator.compare(previousYear, freshYear)
                        totalDifference.addAll(newDiff)
                        println("Differences for this year:${newDiff.size}")
                    }
                }


                if(totalDifference.isNotEmpty()){
                    println("Differences found (ui thread): ${totalDifference.size}")
                    totalDifference.forEach{
                        println(String.format("\t\t type:%s change:%s", it.field.toString(), it.change.toString()))
                    }
                    println("Pushing notification!")
                    notificationFactory.pushNotification(generateDifSummary(totalDifference))
                }
                println("Persisting the year list to the database, from the service!")
                yearGradesService.persistYearGradesModel(webYear)
                println("service finished without errors!")
                jobFinished(jobParams, false)
            }
        })
        return true
    }

    private fun generateDifSummary(diff: List<Difference>): String{
        var notificationContentString = ""
        var lastDifferentMark: GradeModel? = null
        var marksAdded = 0
        var marksChanged = 0
        diff.forEach {
            if(it.field == Difference.Field.Grade && it.change == Difference.FieldChange.Added){
                marksAdded++
                lastDifferentMark = it.supplementary as GradeModel
            }else if(it.field == Difference.Field.Grade && it.change == Difference.FieldChange.Changed){
                marksChanged++
                lastDifferentMark = it.supplementary as GradeModel
            }
        }

        if(marksAdded == 1){
            notificationContentString = String.format(newMarkString, lastDifferentMark?.marks?.last() ?: "")
        }else if(marksAdded > 1){
            notificationContentString = newMarksString
        }else if(marksChanged > 0){
            notificationContentString = markChangedString
        }else{
            notificationContentString = gradeTableChanged
        }

        return notificationContentString
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("onStopJob testing out the jobScheduler API")
        return false
    }

}