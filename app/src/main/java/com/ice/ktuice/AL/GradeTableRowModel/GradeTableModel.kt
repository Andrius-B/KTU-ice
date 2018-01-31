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
     * Appends a mark to the correct table row and cell
     */
    fun addMark(mark: MarkModel){
        val markIdentifier = mark.module_code
        val row: GradeTableRowModel
        if(rowMap.containsKey(markIdentifier)){
            row = rowMap[markIdentifier]!!
        }else{
            row = GradeTableRowModel(ModuleModel(mark)) // extracting the module information from the mark
            rowMap[markIdentifier] = row
        }

        val markWeekModel = WeekModel(mark.week)

        val currentCell = row.getByWeekModel(markWeekModel) // get a cell at the particular column
        if(currentCell?.markModels == null){
            val newCell = GradeTableCellModel(mutableListOf(mark), markWeekModel)
            row.add(newCell)
        }else{
            currentCell.markModels.add(mark)
        }

        if(!weekList.contains(markWeekModel))
            weekList.add(markWeekModel)
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
                if(it.markModels != null && // if the cell contains marks
                        it.markModels.size > 0){
                    val marks = it.markModels
                    marks.forEach {
                        if(!it.isEmpty()) cellEmpty = false
                    }
                }
                if(cellEmpty){
                    row.remove(it)
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
                line += if(it.markModels != null) {
                            it.getDisplayString()
                        }else{
                            emptyMarkMarker
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
        var text = ""
        for ((index, weekModel) in weekList.withIndex()){
            text += weekModel.weekValue.toString() + " => " + weekModel.week
            if(index < weekList.size) text += " | "
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