package com.ice.ktuice.AL.GradeTableRowModel

import com.ice.ktuice.scraper.models.MarkModel

/**
 * Created by Andrius on 1/27/2018.
 * Week model can never be empty and is used for containing information about
 * the table placement information of an empty cell.
 */
class GradeTableCellModel(val markModels:MutableList<MarkModel>?, val weekModel:WeekModel) {
    val isCellEmpty: Boolean = markModels == null

    fun getDisplayString():String{
        val markSeparator = ", "
        var text = ""
        markModels?.forEachIndexed{ index, mark ->
            text += mark.marks.last()
            if(index < markModels.size - 1){
                text += markSeparator
            }
        }
        return text
    }

}