package com.ice.ktuice.al.GradeTable.notifications

import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference

/**
 * Created by Andrius on 3/14/2018.
 */
interface NotificationFactory {
    fun pushNotification(message: String)
}