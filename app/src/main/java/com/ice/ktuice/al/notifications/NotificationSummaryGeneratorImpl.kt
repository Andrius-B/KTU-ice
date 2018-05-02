package com.ice.ktuice.al.notifications

import android.content.Context
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.models.GradeModel

/**
 * Created by Andrius on 2018-03-22.
 */
class NotificationSummaryGeneratorImpl(val context: Context): NotificationSummaryGenerator {


    override fun generateSummaryFromDifferences(differenceList: List<Difference>): String {
        /**
         * the R.string.notification_new_mark_found is a formatted string with a placeholder for a string (which should contain a mark, that was found)
         */
        val newMarkString = context.getString(R.string.notification_new_mark_found)
        val newMarksString = context.getString(R.string.notification_new_marks_found)
        val markChangedString = context.getString(R.string.notification_mark_updated)
        val gradeTableChanged = context.getString(R.string.notification_grade_table_changed)

        var notificationContentString: String
        var lastDifferentMark: GradeModel? = null
        var marksAdded = 0
        var marksChanged = 0
        differenceList.forEach {
            if(it.field == Difference.Field.Grade && it.change == Difference.FieldChange.Added){
                marksAdded++
                lastDifferentMark = it.supplementary as GradeModel
            }else if(it.field == Difference.Field.Grade && it.change == Difference.FieldChange.Changed){
                marksChanged++
                lastDifferentMark = it.supplementary as GradeModel
            }
        }

        notificationContentString = if(marksAdded == 1){
                                        String.format(newMarkString, lastDifferentMark?.marks?.last() ?: "")
                                    }else if(marksAdded > 1){
                                        newMarksString
                                    }else if(marksChanged > 0){
                                        markChangedString
                                    }else{
                                        gradeTableChanged
                                    }

        return notificationContentString
    }
}