package com.ice.ktuice.AL.GradeTableModels

import com.ice.ktuice.scraper.models.GradeResponseModel
import com.ice.ktuice.scraper.models.YearGradesModel
import com.ice.ktuice.scraper.models.YearModel
import java.time.Year

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
        fun buildGradeTableFromYearGradesModel(yearModel: YearGradesModel): GradeTableModel {
            val table = GradeTableModel()
            println("Creating grade table from yearGradesModel, semester len:"+yearModel.semesterList.size)
            yearModel.semesterList.forEach {
                println("adding semester:"+it.semester_number)
                table.addSemester(it)
            }
            table.removeEmptyCells()
            println("Semester count:"+table.semesterList.size)
            return table
        }
    }
}