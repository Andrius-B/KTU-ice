package com.ice.ktuice.AL.GradeTableRowModel

import com.ice.ktuice.scraper.models.MarkResponse

/**
 * Created by Andrius on 1/28/2018.
 */
class GradeTableFactory {
    companion object {
        fun buildGradeTableFromMarkResponse(markResponse: MarkResponse): GradeTableModel{

            //removing spacer cells
            markResponse.filter { it.mark.isEmpty() }
                        .forEach { markResponse.remove(it) }

            val table = GradeTableModel()
            markResponse.forEach { table.addCell(it) }
            return table
        }
    }
}