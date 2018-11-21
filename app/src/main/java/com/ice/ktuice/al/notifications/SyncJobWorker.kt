package com.ice.ktuice.al.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ice.ktuice.R
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 * Note: I moved all of the work to a separate class (SyncJob), for easier testing.
 */
class SyncJobWorker(private val context : Context, private val params : WorkerParameters): Worker(context, params), KoinComponent, AnkoLogger {
    private val syncJob = SyncJob()

    override fun doWork(): Result {
        val notificationsEnabled = inputData.getInt(applicationContext.resources.getString(R.string.notification_enabled_flag), 1)
        try{
        syncJob.sync(notificationsEnabled)
        }catch (nullPtrException: NullPointerException){
            info("Sync task failed..")
            return Result.FAILURE
        }
        return  Result.SUCCESS
    }
}