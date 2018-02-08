package com.ice.ktuice.AL.GradeTableModels

import com.ice.ktuice.scraper.models.GradeResponseModel

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
    }
}