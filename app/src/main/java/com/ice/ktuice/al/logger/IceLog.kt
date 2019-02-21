package com.ice.ktuice.al.logger

import android.util.Log

/**
    A small abstraction to allow debugging the freaking notifications
    pretty much a copy of AnkoLogger, but I need the logging function to be
    overridable in the interface instance

    Ideally this would be named IceLoggerClient or similar, but
    since every class that wants (Ice)Logs has to inherit it, this is nicer.
 */
interface IceLog {
    enum class Target { CONSOLE, FILE }
    val loggerTag: String
        get() = "IceLogger"

    fun log(tag: String, message: String, level: Int, target: Target = Target.CONSOLE){
        if(target == Target.CONSOLE){
            LoggerProvider.logger.log(tag, "$message\n", level)
        }else if(target == Target.FILE){
            LoggerProvider.fileLogger.log(tag, "$message\n", level)
        }
    }

    fun log(tag: String, message: () -> Any?, logLevel: Int){
        this.log(tag, message()?.toString() ?: "null", logLevel)
    }

}

inline fun IceLog.infoFile(message: () -> Any?) {
    val tag = loggerTag
    if (Log.isLoggable(tag, Log.INFO)) {
        this.log(tag, message()?.toString() ?: "null", Log.INFO, IceLog.Target.FILE)
    }
}

fun IceLog.infoFile(message: Any?){
    val tag = loggerTag
    if (Log.isLoggable(tag, Log.INFO)) {
        this.log(tag, message?.toString() ?: "null", Log.INFO, IceLog.Target.FILE)
    }
}

inline fun IceLog.info(message: () -> Any?) {
    val tag = loggerTag
    if (Log.isLoggable(tag, Log.INFO)) {
        this.log(tag, message()?.toString() ?: "null", Log.INFO)
    }
}

fun IceLog.info(message: Any?){
    val tag = loggerTag
    if (Log.isLoggable(tag, Log.INFO)) {
        this.log(tag, message?.toString() ?: "null", Log.INFO)
    }
}

inline fun IceLog.debug(message: () -> Any?) {
    val tag = loggerTag
    this.log(tag, message()?.toString() ?: "null", Log.DEBUG)
}

fun IceLog.debug(message: Any?) {
    val tag = loggerTag
    this.log(tag, message?.toString() ?: "null", Log.DEBUG)
}

inline fun IceLog.warn(message: () -> Any?) {
    val tag = loggerTag
    this.log(tag, message()?.toString() ?: "null", Log.WARN)
}

fun IceLog.warn(message: Any?) {
    val tag = loggerTag
    this.log(tag, message?.toString() ?: "null", Log.WARN)
}