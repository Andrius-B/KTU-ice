package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Created by Andrius on 2/11/2018.
 * The final model that is stored from a request to the server
 */
@RealmClass
open class YearGradesModel(
        year: YearModel = YearModel(),
        var studCode: String = "",
        var dateStamp: Date = Date(), // when was this model fetched from the server
        var loginModel: LoginModel? = LoginModel(),
        var semesterList: RealmList<SemesterModel> = RealmList(),
        /**
         * Stored value of the hash to check validity
         */
        var hashCode: Int = 0
): RealmObject(){
    /**
     * Realm can guarantee that an object will be here,
     * but in code, the YearGradesModel can not exist without a
     * YearModel, thus this is a bypass to keep kotlin inspections clean
     */
    private var _year: YearModel? = year

    val year: YearModel
            get() = _year!!

    /**
     * Concatenate relevant content of the table to a string and then hash it
     * Equality comparison should be performed with the comparator
     */
    override fun hashCode(): Int {
            var contentString = studCode+year.year+year.id
            semesterList
                    .sortedBy { it.semester_number }
                    .forEach{
                            contentString += it.semester
                            it.moduleList
                                    .sortedBy { it.module_code }
                                    .forEach{
                                            contentString += it.module_code
                                            it.grades
                                                    .sortedBy { it.week }
                                                    .forEach{
                                                            it.marks.forEach{
                                                                    contentString += it
                                                            }
                                                    }
                                    }
                    }
            return contentString.hashCode()
    }

    fun convertToGradeList(): List<GradeModel>{
        val list = mutableListOf<GradeModel>()
        this.semesterList.forEach {
            it.moduleList.forEach{
                it.grades.forEach{
                    list.add(it)
                }
            }
        }
        return list
    }


    /**
     * Gets the table of marks as a multi-line string
     * Used for debugging
     */
    override fun toString(): String {
        val tableRowMarker = "\n\r" + "---------------------------------" + "\n\r"
        val columnMarker = " | "
        val emptyMarkMarker = " * "
        var text = tableRowMarker
        this.semesterList.forEach {
            text += "--> ${it.semester} <--\n"
            it.moduleList.forEach {
                var line = it.module_name + columnMarker
                it.grades.forEach {
                    line += if (!it.isEmpty()) {
                        it.marks.toArray().joinToString(", ")
                    } else {
                        emptyMarkMarker
                    }
                    line += columnMarker
                }
                text += line
                text += tableRowMarker
            }
        }
        return text
    }
}