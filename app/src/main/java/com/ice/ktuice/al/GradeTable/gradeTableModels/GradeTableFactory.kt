package com.ice.ktuice.al.GradeTable.gradeTableModels

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.responses.GradeResponseModel
import com.ice.ktuice.models.YearGradesModel

/**
 * Created by Andrius on 1/28/2018.
 * Factory to contain the logic of constructing the Grade Table
 * from markResponse of the API. (a list of marks)
 */
class GradeTableFactory {
    companion object {
        fun buildGradeTableFromMarkResponse(markResponse: GradeResponseModel): GradeTableModel{
            val table = GradeTableModel()
            markResponse.forEach {
                if(!it.isEmpty()) {
                    table.addMark(it)
                }
            }
            return table
        }
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