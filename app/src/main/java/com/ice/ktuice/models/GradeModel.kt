package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class GradeModel(
        var name: String = "",
        var id: String = "",
        var semester: String = "",
        var module_code: String = "",
        var module_name: String = "",
        var semester_number: String = "",
        var credits: String = "",
        var language: String = "",
        var professor: String = "",
        var typeId: String = "",
        var type: String? = "",
        var week: String = "",
        /**
         * A list of marks: higher index strings are newer overridden marks.
         */
        markList: List<String> = mutableListOf()
): RealmObject() {
        var marks: RealmList<String> = RealmList()
        init{
                marks.addAll(markList)
        }

        fun isEmpty():Boolean{
                var empty = true
                marks.forEach {
                        if(!it.isBlank()) empty = false // and if at least one mark is not blank
                        // the mark is not empty!
                }
                return empty
        }

        fun isOnSameDate(grade: GradeModel): Boolean {
                return week == grade.week
        }

        fun gradesEqual(grade: GradeModel): Boolean {
                var same = true
                this.marks.forEach {
                        if(!grade.marks.contains(it)){
                                same = false
                        }
                }
                return same
        }
}