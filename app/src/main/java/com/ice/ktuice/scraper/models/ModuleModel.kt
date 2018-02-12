package com.ice.ktuice.scraper.models

import org.jsoup.nodes.Element

/**
 * Created by simonas on 9/16/17.
 */

class ModuleModel(
        val semester: String,
        val semester_number: String,
        val module_code: String,
        val module_name: String,
        val credits: String,
        val language: String,
        val misc: String,
        val p1: String?, // aka p1
        val p2: String?, // aka p2
        var grades: MutableList<GradeModel>
) {
    constructor(element: Element): this(
            semester = element.getSemester(),
            semester_number = element.getSemesterNumber(),
            module_code = element.getModuleCode(),
            module_name = element.getModuleName(),
            credits = element.getCredits(),
            language = element.getLanguage(),
            misc = element.getMisc(),
            p1 = element.getP1(),
            p2 = element.getP2(),
            grades = mutableListOf()
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
            grades = mutableListOf(gradeModel)
    )

}

private fun Element.getSemester()
        = parent().parent().children().first().children().first().text().split("(")[0].trim()

private fun Element.getSemesterNumber()
        = parent().parent().children().first().children().first().text().split("(")[1].split(')')[0].trim()

private fun Element.getModuleCode()
        = children().first().text()

private fun Element.getModuleName()
        = children().eq(1).text()

private fun Element.getCredits()
        = children().eq(3).text()

private fun Element.getLanguage()
        = children().eq(4).text()

private fun Element.getMisc()
        = children().eq(5).text()

private fun Element.getP1(): String? {
    val jsFunction = children().getOrNull(5)?.children()?.first()?.attr("onclick")
    if (jsFunction != null) {
        val split = "([0-9]*)(?:,')(.*)(?:'\\);)".toRegex().find(jsFunction)
        return split?.groupValues?.getOrNull(1)
    }
    return null
}

private fun Element.getP2(): String? {
    val jsFunction = children().getOrNull(5)?.children()?.first()?.attr("onclick")
    if (jsFunction != null) {
        val split = "([0-9]*)(?:,')(.*)(?:'\\);)".toRegex().find(jsFunction)
        return split?.groupValues?.getOrNull(2)
    }
    return null
}