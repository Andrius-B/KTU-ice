package com.ice.ktuice.al.gradeTable.yearGradesModelComparator

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 */
interface YearGradesModelComparator {
    fun compare(previous: YearGradesModel, new:YearGradesModel): List<Difference>

    fun compare(previuos: YearGradesCollectionModel, new: YearGradesCollectionModel): List<Difference>
}