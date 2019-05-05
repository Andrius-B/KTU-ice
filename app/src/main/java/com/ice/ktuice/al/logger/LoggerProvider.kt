package com.ice.ktuice.al.logger

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.ice.ktuice.App
import com.ice.ktuice.ui.main.MainActivity

class LoggerProvider{
    companion object {
        private val _consoleLogger: Logger = ConsoleIceLoggerImpl()
        private val _fileLogger: Logger = FileIceLoggerImpl(App.getContext()!!)
        val logger: Logger
            get() = _consoleLogger
        val fileLogger: Logger
            get() = _fileLogger
    }

}