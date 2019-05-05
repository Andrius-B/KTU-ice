package com.ice.ktuice.al.gradeTable.yearGradesModelComparator

import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 2/20/2018.
 */
class YearGradesModelComparatorImpl: YearGradesModelComparator, IceLog {

    override fun compare(previous: YearGradesModel, new:YearGradesModel): List<Difference>{
        val diff = mutableListOf<Difference>()

        val sameYear = previous.year.equals(new.year)

        if(!sameYear)throw IllegalArgumentException("Can not compare YearGradesModels of different years!")

        val newList = new.convertToGradeList()
        val prevList = previous.convertToGradeList()

        newList.forEach{
            val newGrade = it

            if(prevList.find{it.isOnSameDate(newGrade)} == null){
                /**
                 * Case where there was no grade on the same date before this latest update
                 */
                diff.add(
                    Difference(Difference.Field.Grade, Difference.FieldChange.Added, it)
                )
            }else{
                val oldGrade = prevList.find{ it.isOnSameDate(newGrade) && newGrade.gradesEqual(it) }
                if(oldGrade == null){
                    /**
                     * Case where there was a mark, and it now is changed
                     */
                    diff.add(Difference(Difference.Field.Grade, Difference.FieldChange.Changed, newGrade))
                }
            }
        }
        return diff
    }

    private fun getMarkCount(model: YearGradesModel): Int {
        return model.convertToGradeList().size
    }

    override fun compare(previous: YearGradesCollectionModel, new: YearGradesCollectionModel): List<Difference> {
        val totalDifference = mutableListOf<Difference>()

        new.yearList.forEach {
            val freshYear = it
            val previousYear = previous.find { it.year.equals(freshYear.year) }
            if(previousYear != null) {
                val newDiff = compare(previousYear, freshYear)
                totalDifference.addAll(newDiff)
            }
        }
        return  totalDifference
    }

}