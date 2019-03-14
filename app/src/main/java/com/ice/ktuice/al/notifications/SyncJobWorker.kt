package com.ice.ktuice.al.notifications

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ice.ktuice.R
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.infoFile
import com.ice.ktuice.al.settings.AppSettings
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 * Note: I moved all of the work to a separate class (SyncJob), for easier testing.
 */
class SyncJobWorker(context : Context, params : WorkerParameters): Worker(context, params), KoinComponent, IceLog {
    private val syncJob = SyncJob()
    override fun doWork(): Result {
        infoFile("Starting SyncJobWorker")
        val notificationsEnabled = inputData.getInt(applicationContext.resources.getString(R.string.notification_enabled_flag), 1)
        try{
            syncJob.sync(notificationsEnabled)
        }catch (nullPtrException: NullPointerException){
            infoFile { "Sync task failed.." }
            return ListenableWorker.Result.failure()
        }
        infoFile("Sync worker done!")
        return  ListenableWorker.Result.success()
    }
}