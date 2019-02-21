package com.ice.ktuice.al.logger

interface Logger {
    /**
     * A very basic interface, to allow log target switching
     */
    fun log(tag: String, message: String, level: Int)
}