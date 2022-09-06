package com.ice.ktuice.al.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ice.ktuice.R
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import org.koin.core.component.KoinComponent

/**
 * Created by Andrius on 2/7/2018.
 * A job service to be used for polling the KTU AIS about whether there are any new grades
 * Note: I moved all of the work to a separate class (SyncJob), for easier testing.
 */
class SyncJobWorker(context : Context, params : WorkerParameters): Worker(context, params),
    KoinComponent, IceLog {
    private val syncJob = SyncJob()
    override fun doWork(): Result {
        val notificationsEnabled = inputData.getInt(applicationContext.resources.getString(R.string.notification_enabled_flag), 1)
        try{
            syncJob.sync(notificationsEnabled)
        }catch (nullPtrException: NullPointerException){
            info("Sync task failed..")
            return Result.failure()
        }
        return  Result.success()
    }
}