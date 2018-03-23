package com.ice.ktuice.al.GradeTable.notifications

/**
 * Created by Andrius on 3/14/2018.
 * A simplistic service to push streamlined notifications
 */
interface NotificationFactory {
    fun pushNotification(message: String)
}