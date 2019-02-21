package com.ice.ktuice.al.gradeTable.gradeTableModels

import com.ice.ktuice.models.YearGradesCollectionModel

/**
 * Created by Andrius on 1/28/2018.
 * Factory to contain the logic of constructing the Grade Table
 * from markResponse of the API. (a list of marks)
 */
class GradeTableFactory {
    companion object {
        fun buildGradeTableFromYearGradesModel(yearGradesList: YearGradesCollectionModel): GradeTableModel {
            val table = GradeTableModel()
            yearGradesList.forEach {
                val yearGrades = it
                yearGrades.semesterList.forEach {
                    table.addSemester(it, yearGrades.year)
                }
            }
            table.removeEmptyCells()
            return table
        }
    }
}