package com.ice.ktuice.al.notifications

import android.app.job.JobParameters
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
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
class SyncJobWorker(private val context : Context, private val params : WorkerParameters): Worker(context, params), KoinComponent, AnkoLogger {

    private val yearGradesService: YearGradesService by inject()
    private val yearGradesComparator: YearGradesModelComparator by inject()
    private val notificationFactory: NotificationFactory by inject()
    private val notificationSummaryGenerator:NotificationSummaryGenerator by inject()

    private val syncJob = SyncJob();

    override fun doWork(): Result {
        val notificationsEnabled = inputData.getInt(applicationContext.resources.getString(R.string.notification_enabled_flag), 1)
        syncJob.sync(notificationsEnabled)
        return  Result.SUCCESS
    }
}