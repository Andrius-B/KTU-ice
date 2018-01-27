package com.ice.ktuice.AL.GradeTableRowModel

import com.ice.ktuice.scraper.models.MarkModel
import com.ice.ktuice.scraper.models.ModuleModel

/**
 * Class used for storing marks of a single module
 * This container class is a list of nullable marks and has a property
 * for the module information, of which this row grades consist.
 */
class GradeTableRowModel(val moduleModel: ModuleModel): ArrayList<GradeTableCellModel>(){
    fun sortByWeek(){
        this.sortBy { it.weekModel.weekValue }
    }
}