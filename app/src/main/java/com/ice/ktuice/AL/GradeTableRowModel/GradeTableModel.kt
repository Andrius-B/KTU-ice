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
    fun getTotalWeekList(): List<WeekModel>{ // public getter to expose the seen weekModel list
        return weekList.toList()
    }

    /**
     * Appends a mark to the correct table row
     */
    fun addCell(mark: MarkModel){
        val cellModel = GradeTableCellModel(mark, WeekModel(mark.week))
        println(String.format("Adding mark %s at %s", mark.marks.last(), mark.week))
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

    /**
     * Fair warning - this list might contain multiple marks with on the same week
     * @return a list of GradeTableRowModel
     */
    fun getRows(): List<GradeTableRowModel>{
        val rowList = mutableListOf<GradeTableRowModel>()
        rowMap.forEach{
            rowList.add(it.value)
        }
        return rowList.toList()
    }

    /**
     * Removes all empty cells (empty here meaning, the grade string list is empty)
     */
    fun removeEmptyCells(){
        rowMap.forEach {
            val row = it.value
            row.forEach {
                var cellEmpty = true
                if(it.markModel?.marks != null && // if the cell contains marks
                        it.markModel.marks.size > 0){
                    val marks = it.markModel.marks
                    marks.forEach {
                        if(!it.isBlank()) cellEmpty = false // and if atleast one mark is not blank
                        // the mark is not empty!
                    }
                }
            }
        }
        sortByWeekValue()
    }

    private fun sortByWeekValue(){
        rowMap.forEach {
            it.value.sortByWeek()
        }
    }

    /**
     * Gets the table of marks as a multi-line string
     * Used for debugging
     */
    override fun toString(): String {
        val tableRowMarker = "\n\r" + "---------------------------------" + "\n\r"
        val columnMarker = " | "
        val markSeparator = ", "
        val emptyMarkMarker = " * "
        var text = tableRowMarker
        rowMap.forEach {
            var line = it.value.moduleModel.module_name + columnMarker
            it.value.forEach {
                if(it.markModel != null) {
                    var markIndex = 0
                    for(markString in it.markModel.marks) {
                        markIndex++
                        line += markString
                        if (markIndex < it.markModel.marks.size) line += markSeparator
                    }
                }else{
                    line += emptyMarkMarker
                }
                line += columnMarker
            }
            text += line
            text += tableRowMarker
        }
        return text
    }

    /**
     *  Gets the seen week model list as string
     *  Used for debugging
     */
    fun getWeekListString(): String{
        weekList.sortBy { it.weekValue }
        var index = 0
        var text = ""
        for (weekModel in weekList){
            text += weekModel.weekValue.toString() + " => " + weekModel.week
            if(index < weekList.size) text += " | "
            index ++
        }
        return text
    }

    fun printRowCounts(){
        rowMap.forEach{
            println(String.format("%s : %d", it.value.moduleModel.module_name, it.value.size))
            println("Week model count:"+weekList.size)
        }
    }

}