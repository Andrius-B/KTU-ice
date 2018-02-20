package com.ice.ktuice.AL.GradeTable.yearGradesModelComparator

import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 */
class YearGradesModelComparatorImpl: YearGradesModelComparator {
    override fun compare(previous: YearGradesModel, new:YearGradesModel): List<Difference>{
        val diff = mutableListOf<Difference>()

        val sameYear = previous.year.equals(new.year)
        val semesterCountDifference = new.semesterList.size - previous.semesterList.size
        val markCountDifference = getMarkCount(new) - getMarkCount(previous)

        if(!sameYear)throw IllegalArgumentException("Can not compare YearGradesModels of different years!")

        (0..semesterCountDifference)
                .map {
                    if(it >0) Difference.FieldChange.Added
                    else Difference.FieldChange.Removed
                }
                .forEach { diff.add(Difference(Difference.Field.Semester, it)) }

        (0..markCountDifference)
                .map {
                    if(it >0) Difference.FieldChange.Added
                    else Difference.FieldChange.Removed
                }
                .forEach { diff.add(Difference(Difference.Field.Grade, it)) }

        return diff
    }

    private fun getMarkCount(model: YearGradesModel): Int {
        var markCount = 0
        model.semesterList.forEach {
            it.moduleList.forEach {
                it.grades.forEach {
                    markCount += it.marks.size
                }
            }
        }
        return markCount
    }
}