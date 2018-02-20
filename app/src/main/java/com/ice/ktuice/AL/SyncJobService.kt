package com.ice.ktuice.AL

import android.app.job.JobParameters
import android.app.job.JobService
import com.ice.ktuice.AL.GradeTable.GradeTableManager
import com.ice.ktuice.AL.GradeTable.NotificationFactory
import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 */
class SyncJobService: JobService(), KoinComponent {

    private val preferenceRepository: PreferenceRepository by inject()
    private val loginRepository: LoginRepository by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()

    private var jobParams: JobParameters? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        println("onStartJob testing out the jobScheduler API")
        jobParams = params
        val tableManager = GradeTableManager()
        doAsync({
            println(it.getStackTraceString())
            jobFinished(jobParams, false)
        },{
            println("Getting logged in user on the service!")
            val login = tableManager.getLoginForCurrentUser()
            println("gettring grade table on the service!")
            val yearList = tableManager.getYearGradesListFromWeb(login)
            val dbYearList = tableManager.getYearGradesListFromDB(login)

            val totalDifference = mutableListOf<Difference>()

            yearList.forEach {
                val freshYear = it
                val previousYear = dbYearList.find { it.year.equals(freshYear.year) }
                if(previousYear != null) {
                    totalDifference.addAll(yearGradesComparator.compare(previousYear, freshYear))
                }
            }

            uiThread {
                println("Differences found:" + totalDifference.size)
                if(totalDifference.size > 0){
                    NotificationFactory(applicationContext).pushNotification(generateDifSummary(totalDifference))
                }
                println("Persisting the year list to the database, from the service!")
                tableManager.persistYearGradeModels(yearList)
            }
            println("service finished without errors!")
            jobFinished(jobParams, false)
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