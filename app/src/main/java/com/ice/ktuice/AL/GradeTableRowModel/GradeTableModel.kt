package com.ice.ktuice.AL.GradeTableRowModel

import com.ice.ktuice.scraper.models.MarkModel
import com.ice.ktuice.scraper.models.ModuleModel

/**
 * Created by Andrius on 1/26/2018.
 * 
 * The marks are put into this container list for sorting / preparing for display
 * To be more specific:
 *      -Firstly we sort by mark type, adding to respective lists in the map, identified by the module id
 *      -Then we sort by week, a numerical estimation of it
 * When taking out of the model, we request by starting date and scan though all the modules
 * to find a mark on the same day or as close as possible.
 */
class GradeTableModel() {
    private val rowMap: HashMap<String, GradeTableRowModel> = HashMap()
    private val weekList = mutableListOf<WeekModel>()
    private var sorted = false

    /**
     * Appends a mark to the correct table row
     */
    fun addCell(mark: MarkModel){
        val cellModel = GradeTableCellModel(mark, WeekModel(mark.week))

        sorted = false
        if(!weekList.contains(cellModel.weekModel)){
            weekList.add(cellModel.weekModel)
        }
        val markIdentifier = mark.module_code
        if(rowMap.containsKey(markIdentifier)){
            rowMap[markIdentifier]?.add(cellModel)
        }else{
            val newRow = GradeTableRowModel(ModuleModel(mark)) // extracting the module information from the mark
            newRow.add(cellModel)
            rowMap[markIdentifier] = newRow
        }
    }

    override fun toString(): String {
        val tableRowMarker = "\n\r" + "---------------------------------" + "\n\r"
        val columnMarker = " | "
        var text = tableRowMarker
        rowMap.forEach {
            var line = it.value.moduleModel.module_name + columnMarker
            it.value.forEach {
                if(it.markModel != null) {
                    line += it.markModel.mark + columnMarker
                }
            }
            text += line
            text += tableRowMarker
        }
        return text
    }

}