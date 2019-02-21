package com.ice.ktuice.al.logger

import android.content.Context
import android.util.Log

class FileLogReader(private val context: Context) {
    fun printFile(path: String){
        val logger = LoggerProvider.logger
        val fs = context.openFileInput(path)
        fs.reader().buffered().use{
            while (it.ready()){
                logger.log("FileLog", it.readLine(),  Log.INFO)
            }
        }
    }
}