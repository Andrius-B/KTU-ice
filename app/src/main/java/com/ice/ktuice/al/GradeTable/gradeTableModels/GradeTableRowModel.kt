package com.ice.ktuice.al.GradeTable.gradeTableModels

import com.ice.ktuice.models.ModuleModel

/**
 * Class used for storing marks of a single module
 * This container class is a list of nullable marks and has a property
 * for the module information, of which this row grades consist.
 */
class GradeTableRowModel(val moduleModel: ModuleModel): ArrayList<GradeTableCellModel>(){
    /**
     * Gets a single cell by week model.
     * Only a single cell should exist in one row for a weekModel.
     */
    fun getByWeekModel(weekModel: WeekModel): GradeTableCellModel?{
        return singleOrNull { it.weekModel.equals(weekModel) }
    }

    /**
     * Checks if this list of gradeTableCells does not contain more than one
     * cell for a single weekModel
     */
    fun isRowCellListValid(): Boolean{
        var valid = true
        forEach {
            val iter = it
            if(filter { it.weekModel.equals(iter.weekModel) }.size > 1) valid = false
        }
        return valid
    }
}