package com.ice.ktuice.al.services.scraperService.ktuScraperService.handlers

import com.ice.ktuice.models.*
import com.ice.ktuice.models.responses.ModuleResponseModel
import com.ice.ktuice.models.responses.YearGradesResponseModel
import com.ice.ktuice.al.services.scraperService.exceptions.AuthenticationException
import io.realm.RealmList
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class DataHandler {
    companion object: AnkoLogger{
        fun getGrades(loginModel: LoginModel, planYear: YearModel)
                = getGrades(loginModel, planYear.year, planYear.id)

        private fun getGrades(loginModel: LoginModel, planYear: String, yearId: String): YearGradesResponseModel {
            info("Getting grades @DataHandler!")
            val moduleResponse = getModules(loginModel, planYear, yearId)
            info("Modules found:"+moduleResponse.size)
            val yearGrades = YearGradesModel(YearModel(yearId, planYear), loginModel = loginModel)
            moduleResponse.forEach { moduleModel ->
                /*
                    Get grades for each module, and then add the modules to the appropriate semester
                 */
                moduleModel.grades = getModuleMarkList(loginModel, moduleModel)
                var semesterModel: SemesterModel? = null;
                for(semester in yearGrades.semesterList){
                    if(semester.semester == moduleModel.semester &&
                       semester.semester_number == moduleModel.semester_number){
                        semesterModel = semester
                    }
                }

                if(semesterModel == null) {
                    semesterModel = SemesterModel(moduleModel.semester, moduleModel.semester_number, RealmList())
                    yearGrades.semesterList.add(semesterModel)
                }

                semesterModel.moduleList.add(moduleModel)
            }
            yearGrades.studCode = loginModel.studentId
            yearGrades.dateStamp = Date()
            yearGrades.hashCode = yearGrades.hashCode()
            return YearGradesResponseModel(moduleResponse.statusCode, yearGrades)
        }

        private fun getModules(loginModel: LoginModel, planYear: String, studId: String): ModuleResponseModel {
            val url = "https://uais.cr.ktu.lt/ktuis/STUD_SS2.planas_busenos?" +
                    "plano_metai=$planYear&" +
                    "p_stud_id=$studId"

            val request = Jsoup.connect(url)
                    .cookies(loginModel.getCookieMap())
                    .method(Connection.Method.GET)
                    .execute()

            request.charset("windows-1257")
            val parse = request.parse()
            val moduleList = if(UnauthenticatedRequestDetector.isResponseAuthError(parse)){
                throw AuthenticationException("Request not authenticated!")
            }else{
                ModuleResponseModel(request.statusCode())
            }

            val moduleTables = parse.select(".table.table-hover > tbody > tr")
            if (moduleTables.size > 0) {
                /*
                Even though the semester number is discarded when selecting all table rows (from both tables)
                it is parsed later via the parent selectors.
                 */
                moduleTables.forEach{
                    val model = ModuleModel(it)
                    moduleList.add(model)
                }
            }

            return moduleList
        }

        private fun getModuleMarkList(loginModel: LoginModel, moduleModel: ModuleModel): RealmList<GradeModel> {
            val markList = RealmList<GradeModel>()
            val url = "https://uais.cr.ktu.lt/ktuis/STUD_SS2.infivert"
            val request = Jsoup.connect(url)
                    .cookies(loginModel.getCookieMap())
                    .method(Connection.Method.POST)
                    .data(mapOf(
                            "p1" to moduleModel.p1,
                            "p2" to moduleModel.p2
                    ))
                    .execute()

            request.charset("windows-1257")
            val parse = request.parse()

            val markTable = parse.select(".d_grd2[style=\"border-collapse:collapse; empty-cells:hide;\"]").firstOrNull()
            val markInfoTable = parse.select(".d_grd2[style=\"border-collapse:collapse; table-layout:fixed; width:450px;\"]").firstOrNull()
            val headerInfo = parse.select("blockquote").select("p")

            if (markTable != null && markInfoTable != null) {
                val markTypeIdList: List<String> = getMarkTypeIdList(markTable)
                val infoTypeRowList: Map<String, String> = getMarkTypeMap(markInfoTable)
                val markWeekList: List<String> = getMarkWeekList(markTable)
                val markDataList: Map<Int, List<String>> = getMarkDataMap(markTable)
                val professorText = headerInfo[2].text()

                (0 until markWeekList.size-1).forEach { index ->
                    if (markTypeIdList[index] != " ") {

                        val markModel = GradeModel(
                                name = moduleModel.module_name,
                                id = moduleModel.module_code,
                                semester = moduleModel.semester,
                                module_code = moduleModel.module_code,
                                module_name = moduleModel.module_name,
                                semester_number = moduleModel.semester_number,
                                credits = moduleModel.credits,
                                language = moduleModel.language,
                                professor = professorText,
                                typeId = markTypeIdList[index],
                                type = infoTypeRowList[markTypeIdList[index]],
                                week = markWeekList[index],
                                markList = markDataList[index] ?: listOf()
                        )
                        markList.add(markModel)
                    }
                }
            }
            return markList
        }

        private fun getMarkTypeMap(element: Element): Map<String, String> {
            return mutableMapOf<String, String>().apply {
                element.select("tr.dtr").forEach { typeElement ->
                    val key = typeElement.children()[0].text()
                    val value = typeElement.children()[1].text()
                    put(key, value)
                }
            }
        }

        private fun getMarkWeekList(element: Element): MutableList<String> {
            val rowList = element.select("tr")
            return mutableListOf<String>().apply {
                val headerWeekRow = rowList[0].children()
                        /*
                        Ignore the first 4 values, because in this row they are:
                        Number, Group, Name and "Week" respectively

                        Also ignore the last two children, because they are Suggested and final
                        marks, which are not needed in this table.
                         */
                        .subList(4, rowList[0].children().size - 1)
                headerWeekRow.forEach { cell ->
                    add(cell.text())
                }
            }
        }

        private fun getMarkTypeIdList(element: Element): MutableList<String> {
            val rowList = element.select("tr")
            return mutableListOf<String>().apply {
                val headerTypeRow = rowList[1].children()
                        /*
                        Ignore the first child, because its "Task"
                        and the last three, because they are not needed ("Įsk", "Paž." and "Data")
                         */
                        .subList(1, rowList[1].children().size - 2)
                headerTypeRow.forEach { cell ->
                    add(cell.text())
                }
            }
        }

        private fun getMarkDataMap(element: Element): Map<Int, List<String>> {
            val rowList = element.select("tr")
            return mutableMapOf<Int, MutableList<String>>().apply {
                (2 until rowList.size).forEach { rowIndex ->
                    var headerDataRow = rowList[rowIndex].children().toList()
                    if (rowIndex == 2) {
                        /*
                         The first row of marks (row #2) also contains the suggested totals for the
                         semester, which are not relevant for this particular request, these are ignored by the -4 in size
                         Also the first data row contains student data, such as name and the academic group id, which are discarded.
                         */
                        headerDataRow = headerDataRow.subList(4, rowList[rowIndex].children().size - 3)
                    } else {
                        /*
                         Other rows have a single element with colspan of 4, so only the first child must be discarded,
                         and only the last column spans the suggestions group above.
                         */
                        headerDataRow = headerDataRow.subList(1, rowList[rowIndex].children().size)
                    }
                    headerDataRow.forEachIndexed { index, cell ->
                        val text = cell.text()
                        if (text.isNotBlank()) {
                            if (containsKey(index)) {
                                put(index, get(index)!!.apply { add(text) })
                            } else {
                                put(index, mutableListOf(text))
                            }
                        }
                    }
                }
            }
        }

    }
}