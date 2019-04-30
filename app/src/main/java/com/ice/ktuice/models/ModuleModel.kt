package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.jsoup.nodes.Element

/**
 * Created by simonas on 9/16/17.
 * Data class and parsing help for the module information
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
        var p1: String? = "",
        var p2: String? = "",
        var p3: String? = "",
        var grades: RealmList<GradeModel> = RealmList()
):RealmObject() {

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
            p3 = getP3(element),
            grades = RealmList<GradeModel>()
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
            grades = RealmList(gradeModel)
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

        private fun getInfivertJsFunction(e: Element)
                = e.children().getOrNull(5)?.children()?.first()?.attr("onclick")

        private fun getInfivertJsFunctionArgument(jsFunction: String?, index: Int): String? {
            if(jsFunction != null) {
                val arguments = """\(.*\)""".toRegex().find(jsFunction)?.value?.removeSurrounding("(", ")")?.split(',')
                return removeChars(arguments?.get(index), "\'\"")
            }
            return null
        }

        private fun getP1(e: Element)
                = getInfivertJsFunctionArgument(getInfivertJsFunction(e), 0)

        private fun getP2(e: Element)
                = getInfivertJsFunctionArgument(getInfivertJsFunction(e), 1)

        private fun getP3(e: Element)
                = getInfivertJsFunctionArgument(getInfivertJsFunction(e), 2)

        private fun removeChars(target: String?, chars: String): String? {
            var result = target
            for(c in chars){
                result = result?.replace(c.toString(), "")
            }
            return result
        }
    }
}