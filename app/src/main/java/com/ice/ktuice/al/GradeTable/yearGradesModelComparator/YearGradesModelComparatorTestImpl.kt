package com.ice.ktuice.al.GradeTable.yearGradesModelComparator

import com.ice.ktuice.models.GradeModel
import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 * Used for testing out notifications, always reports a new grade to be added
 */
class YearGradesModelComparatorTestImpl: YearGradesModelComparator{
    override fun compare(previous: YearGradesModel, new: YearGradesModel): List<Difference>{
        val diff = mutableListOf<Difference>()

        val sameYear = previous.year.equals(new.year)
        if(!sameYear) throw IllegalArgumentException("Can not compare YearGradesModels of different years!")

        /**
         * A grade for testing
         */
        val testGrade = GradeModel(
                "Test",
                "0",
                "Pavasario Semestras 2017",
                "PB1850560",
                "ASU",
                "1",
                "0",
                "LT",
                "Vardenis Pavardenis",
                "1",
                "KD",
                "17",
                listOf("10"))

        diff.add(Difference(Difference.Field.Grade, Difference.FieldChange.Added, testGrade))
        return diff
    }
}