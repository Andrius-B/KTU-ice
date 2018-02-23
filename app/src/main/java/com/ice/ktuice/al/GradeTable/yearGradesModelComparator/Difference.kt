package com.ice.ktuice.al.GradeTable.yearGradesModelComparator

/**
 * Created by Andrius on 2/20/2018.
 * This class should be able to fully describe a difference between two YearGradesModels
 */
class Difference(val field: Field, val change: FieldChange, val supplementary: Any? = null){
    enum class FieldChange{ Added, Removed, Changed }
    enum class Field { Semester, Grade }
}