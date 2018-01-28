package com.ice.ktuice.AL.GradeTableRowModel

import com.ice.ktuice.scraper.models.MarkResponse

/**
 * Created by Andrius on 1/28/2018.
 * Factory to contain the logic of constructing the Grade Table
 * from markResponse of the API. (a list of marks)
 */
class GradeTableFactory {
    companion object {
        fun buildGradeTableFromMarkResponse(markResponse: MarkResponse): GradeTableModel{

            //removing spacer cells
            markResponse.filter { it.marks.isEmpty() }
                        .forEach { markResponse.remove(it) }

            markResponse.forEach {
                val markList = it.marks
                it.marks.filter { it.isEmpty() }
                        .forEach { markList.remove(it)}

            }

            val table = GradeTableModel()
            markResponse.forEach { table.addCell(it) }
            return table
        }
    }
}