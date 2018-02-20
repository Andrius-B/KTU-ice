package com.ice.ktuice.AL.GradeTable.yearGradesModelComparator

import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 */
interface YearGradesModelComparator {
    fun compare(previous: YearGradesModel, new:YearGradesModel): List<Difference>
}