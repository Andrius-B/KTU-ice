package com.ice.ktuice.AL.GradeTableModels

import com.ice.ktuice.scraper.models.GradeModel
import com.ice.ktuice.scraper.models.ModuleModel

/**
 * Created by Andrius on 2/11/2018.
 */
class GradeTableSemesterModel(val semester: String, val semester_number:String) {
    private val rowMap: HashMap<String, GradeTableRowModel> = HashMap()
    private val weekList = mutableListOf<WeekModel>()
    fun getTotalWeekList(): List<WeekModel>{ // public getter to expose the seen weekModel list
        return weekList.toList()
    }

    /**
     * Appends a grade to the correct table row and cell
     */
    fun addMark(grade: GradeModel){
        val moduleIdentifier = grade.module_code
        val row: GradeTableRowModel
        if(rowMap.containsKey(moduleIdentifier)){
            row = rowMap[moduleIdentifier]!!
        }else{
            row = GradeTableRowModel(ModuleModel(grade)) // extracting the module information from the grade
            rowMap[moduleIdentifier] = row
        }

        val markWeekModel = WeekModel(grade.week)

        val currentCell = row.getByWeekModel(markWeekModel) // get a cell at the particular column
        if(currentCell?.gradeModels == null){
            val newCell = GradeTableCellModel(mutableListOf(grade), markWeekModel)
            row.add(newCell)
        }else{
            currentCell.gradeModels.add(grade)
        }

        if(!weekList.contains(markWeekModel))
            weekList.add(markWeekModel)
    }

    fun addModule(module: ModuleModel){
        val moduleIdentifier = module.module_code
        val row: GradeTableRowModel
        if(rowMap.containsKey(moduleIdentifier)){
            row = rowMap[moduleIdentifier]!!
        }else{
            row = GradeTableRowModel(module) // extracting the module information from the grade
            rowMap[moduleIdentifier] = row
        }

        module.grades.forEach{
            addMark(it)
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
        val iterator = rowMap.iterator()
        if(iterator.hasNext()){
            val it = iterator.next()
            val row = it.value
            val cellIterator = row.iterator()
            while(cellIterator.hasNext()){
                val cell = cellIterator.next()
                if(cell.isEmpty()){
                    cellIterator.remove()
                }
            }
        }
        removeEmptyWeekModels() // after empty grades are removed, we can discard empty columns in the table
        sortByWeekValue()
    }

    fun removeEmptyWeekModels(){
        val weekIterator = weekList.iterator()
        while(weekIterator.hasNext()){
            var weekEmpty = true
            val week = weekIterator.next()

            getRows().forEach {
                val weekGrade = it.getByWeekModel(week)
                if(weekGrade?.isEmpty() == false) {
                    weekEmpty = false
                }
            }

            if(weekEmpty){
                weekIterator.remove()
            }
        }
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
                line += if(!it.isEmpty()) {
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