package com.ice.ktuice.al.logger

import android.content.Context
import android.icu.text.DateFormat
import android.os.Build
import android.util.Log
import io.realm.log.RealmLog.info
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FileIceLoggerImpl(private val context: Context): Logger {
    override fun log(tag: String, message: String, level: Int) {
        context.openFileOutput("ice_log.log", Context.MODE_APPEND).writer().use{
            if (Log.isLoggable(tag, level)) {
                val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DateFormat.getDateTimeInstance().format(Date())
                } else {
                    "--"
                }
                val logLine: String = if(level == Log.INFO){
                    "$now INFO: $message"
                }else if(level == Log.WARN){
                    "$now DEBUG: $message"
                }else if(level == Log.DEBUG){
                    "$now DEBUG: $message"
                }else{
                    throw IllegalArgumentException("Invalid log level given to FileIceLoggerImpl!")
                }
                it.write(logLine)
                Log.i("FileLogger/INFO", logLine)
            }
        }
    }
}