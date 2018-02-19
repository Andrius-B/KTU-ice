package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.jsoup.nodes.Element

/**
 * Created by simonas on 9/16/17.
 */

@RealmClass
open class ModuleModel(
        var semester: String = "",
        var semester_number: String = "",
        @PrimaryKey
        var module_code: String = "",
        var module_name: String = "",
        var credits: String = "",
        var language: String = "",
        var misc: String = "",
        var p1: String? = "", // aka p1
        var p2: String? = "", // aka p2
        gradesList: List<GradeModel> = listOf()
):RealmObject() {

    private var _grades: RealmList<GradeModel> = RealmList()
    var grades: List<GradeModel>
        get(){
            val list = mutableListOf<GradeModel>()
            _grades.forEach{ list.add(it) }
            return list
        }
        set(value){
            _grades = RealmList()
            value.forEach { _grades.add(it) }
        }
    init{
        gradesList.forEach {
            _grades.add(it)
        }
    }

    constructor(element: Element): this(
            semester = getSemester(element),
            semester_number = getSemesterNumber(element),
            module_code = getModuleCode(element),
            module_name = getModuleName(element),
            credits = getCredits(element),
            language = getLanguage(element),
            misc = getMisc(element),
            p1 = getP1(element),
            p2 = getP2(element),
            gradesList = RealmList()
    )

    //since the model information is embedded into the mark model, this constructor makes sense.
    constructor(gradeModel: GradeModel): this(
            semester = gradeModel.semester,
            semester_number = gradeModel.semester_number,
            module_code = gradeModel.module_code,
            module_name = gradeModel.module_name,
            credits = gradeModel.credits,
            language = gradeModel.language,
            misc = "",
            p1 = null,
            p2 = null,
            gradesList = RealmList(gradeModel)
    )

    /**
     * static helpers to parse the html document for construction
     */
    companion object {

        private fun getSemester(e: Element)
                = e.parent().parent().children().first().children().first().text().split("(")[0].trim()

        private fun getSemesterNumber(e: Element)
                = e.parent().parent().children().first().children().first().text().split("(")[1].split(')')[0].trim()

        private fun getModuleCode(e: Element)
                = e.children().first().text()

        private fun getModuleName(e: Element)
                = e.children().eq(1).text()

        private fun getCredits(e: Element)
                = e.children().eq(3).text()

        private fun getLanguage(e: Element)
                = e.children().eq(4).text()

        private fun getMisc(e: Element)
                = e.children().eq(5).text()

        private fun getP1(e: Element): String? {
            val jsFunction = e.children().getOrNull(5)?.children()?.first()?.attr("onclick")
            if (jsFunction != null) {
                val split = "([0-9]*)(?:,')(.*)(?:'\\);)".toRegex().find(jsFunction)
                return split?.groupValues?.getOrNull(1)
            }
            return null
        }

        private fun getP2(e: Element): String? {
            val jsFunction = e.children().getOrNull(5)?.children()?.first()?.attr("onclick")
            if (jsFunction != null) {
                val split = "([0-9]*)(?:,')(.*)(?:'\\);)".toRegex().find(jsFunction)
                return split?.groupValues?.getOrNull(2)
            }
            return null
        }
    }
}