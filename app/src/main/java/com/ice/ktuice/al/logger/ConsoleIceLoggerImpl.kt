package com.ice.ktuice.al.logger

import android.util.Log

/**
 * Logger prints logs to android log console
 * accessible via logcat
 */
class ConsoleIceLoggerImpl: Logger{
    override fun log(tag: String, message: String, level: Int) {
        if (Log.isLoggable(tag, level)) {
            if(level == Log.INFO){
                Log.i(tag, message)
            }else if(level == Log.WARN){
                Log.w(tag, message)
            }else if(level == Log.DEBUG){
                Log.d(tag, message)
            }
        }
    }
}