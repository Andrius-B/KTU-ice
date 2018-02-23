package com.ice.ktuice.al.GradeTable.GradeTableModels

import com.ice.ktuice.models.GradeModel

/**
 * Created by Andrius on 1/27/2018.
 * Week model can never be empty and is used for containing information about
 * the table placement information of an empty cell.
 */
class GradeTableCellModel(val gradeModels:MutableList<GradeModel>, val weekModel:WeekModel) {
    fun getDisplayString():String{
        val markSeparator = ", "
        var text = ""
        gradeModels.forEachIndexed{ index, mark ->
            text += mark.marks.firstOrNull() ?: ""
            if(index < gradeModels.size - 1){
                text += markSeparator
            }
        }
        return text
    }

    fun isEmpty():Boolean{
        var empty = true
        gradeModels.forEach {
            if(it.marks.isNotEmpty()) empty = false
            if(it.marks.firstOrNull()?.isNotBlank() == true)empty = false
        }
        return empty
    }
}