package com.ice.ktuice.al.notifications

import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference

/**
 * Created by Andrius on 2018-03-22.
 */
interface NotificationSummaryGenerator {
    /**
     * This function should generate some reasonable summary of the changes that
     * were detected in the grade table
      */
    fun generateSummaryFromDifferences(differenceList: List<Difference>):String

}