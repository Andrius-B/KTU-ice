package com.ice.ktuice.AL.GradeTable.yearGradesModelComparator

import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 */
class YearGradesModelComparatorTestImpl: YearGradesModelComparator{
    override fun compare(previous: YearGradesModel, new: YearGradesModel): List<Difference>{
        val diff = mutableListOf<Difference>()

        val sameYear = previous.year.equals(new.year)
        if(!sameYear) throw IllegalArgumentException("Can not compare YearGradesModels of different years!")

        diff.add(Difference(Difference.Field.Grade, Difference.FieldChange.Added))
        return diff
    }

}