package com.ice.ktuice.al.GradeTable.gradeTableModels

import com.ice.ktuice.models.GradeModel
import com.ice.ktuice.models.SemesterModel
import com.ice.ktuice.models.YearModel

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
class GradeTableModel(val semesterList: MutableList<GradeTableSemesterModel> = mutableListOf()){
    private var selectedSemester: GradeTableSemesterModel? = semesterList.lastOrNull()

    @Deprecated("Grade table should only be build using semester models!",
                ReplaceWith("GradeTableFactory.buildGradeTableFromYearGradesModel()"))
    fun addMark(grade: GradeModel){
            semesterList.lastOrNull()?.addMark(grade)
    }

    fun addSemester(semester: SemesterModel, yearModel: YearModel){
        val semesterModel = GradeTableSemesterModel(semester.semester, semester.semester_number, yearModel)
        semester.moduleList.forEach {
            semesterModel.addModule(it)
        }
        semesterList.add(semesterModel)
    }

    fun removeEmptyCells(){
        val it = semesterList.iterator()
        while (it.hasNext()){
            it.next().removeEmptyCells()
        }
    }

    fun selectSemester(semester_number: String){
        val index = semesterList.indexOf(semesterList.find{ it.semester_number == semester_number })
        selectSemester(index)
    }
    fun selectSemester(index: Int){
        selectedSemester = semesterList[index]
    }

    /**
     * Delegate most of the uses to the semester model
     */
    fun getRows() = selectedSemester?.getRows()
    fun getTotalWeekList() = selectedSemester?.getTotalWeekList()
    fun getWeekListString() = selectedSemester?.getWeekListString()
    fun printRowCounts() = selectedSemester?.printRowCounts()
    override fun toString() = selectedSemester?.toString() ?: "Grade table with no semester selected!"
}