package com.ice.ktuice.al.gradeTable.gradeTableModels

import com.ice.ktuice.models.GradeModel

/**
 * Created by Andrius on 1/27/2018.
 * Week model can never be empty and is used for containing information about
 * the table placement information of an empty cell.
 */
class GradeTableCellModel(var gradeModels:MutableList<GradeModel>, val weekModel:WeekModel) {
    fun getDisplayString():String{
        val gradeModelsFiltered = gradeModels.filter { !it.marks.lastOrNull().isNullOrBlank() }
        val markSeparator = ", "
        var text = ""
        gradeModelsFiltered.forEachIndexed{ index, mark ->
            val markText = mark.marks.lastOrNull() ?: ""
            if(markText.isNotBlank()){
                text += markText
                if(index < gradeModelsFiltered.size - 1){
                    text += markSeparator
                }
            }
        }
        return text
    }

    fun isEmpty()
        = getDisplayString().isBlank()
}