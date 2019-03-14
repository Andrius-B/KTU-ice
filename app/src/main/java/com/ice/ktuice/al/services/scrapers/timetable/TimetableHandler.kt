package com.ice.ktuice.al.services.scrapers.timetable

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.TimetableModel
import com.ice.ktuice.models.responses.TimetableResponseModel
import com.ice.ktuice.models.timetableModels.TimetableSemester
import com.ice.ktuice.models.timetableModels.TimetableWeek
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

/**
 * This handler is responsible for fetching upcoming tests and the starts for weeks/semesters
 */
class TimetableHandler{
    companion object {
        /**
         * @param login - the authentication required to fetch the timetable
         * @param fetchUpcomingTestsFor -  a list of dates, for which weeks to fetch the upcoming tests
         */
        fun getTimetable(login: LoginModel, fetchUpcomingTestsFor: List<Date>? = listOf()): TimetableResponseModel {
            val urlInitial = String.format("https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0")
            val responseInitial = Jsoup.connect(urlInitial)
                    .cookies(login.getCookieMap())
                    .method(Connection.Method.GET)
                    .execute()
            val documentInitial = responseInitial.parse()
            val (v, n) = findVandN(documentInitial)
            val weekTests = mutableMapOf<TimetableWeek, List<String>>()
            val tm = TimetableModel(
                    findCurrentSemester(documentInitial),
                    findCurrentWeek(documentInitial),
                    getAllSemesters(documentInitial),
                    getAllWeeks(documentInitial),
                    weekTests // filled in later:
            )
            // `n=asm_id&v=119815&w=1&data_nuo=2018.09.03`
//            println("Payload to week request: `n=$n&v=$v&w=1&data_nuo=${tm.currentSemester.semesterStartDateString}`")
            val url = String.format("https://uais.cr.ktu.lt/ktuis/TVARKARASTIS.kalend0")
            fetchUpcomingTestsFor?.forEach{ date ->
                val response = Jsoup.connect(url)
                        .cookies(login.getCookieMap())
                        .data("n", n)
                        .data("v", v)
                        .data("w", "1") // I assume this stands for "week view"
                        .data("data_nuo", TimetableModel.dateFormat.format(date))
                        .method(Connection.Method.POST)
                        .execute()
                val doc = response.parse()
                val week = findCurrentWeek(doc)
                val dls = doc.select("dl")
                if(dls.isNotEmpty()){
                    val dl = dls.last()
                    val tests = mutableListOf<String>()
                    dl.children().forEach {
                        tests.add(it.text())
                    }
                    weekTests[week] = tests
                }
            }
            // TODO add error handling
            return TimetableResponseModel(tm, 200)
        }

        /**
         * Accepts a response document from https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0
         * *the initial request*
         */
        fun findCurrentSemester(doc: Document): TimetableSemester{
            val elem = doc.body().getElementById("prm1_id").select("option[selected]")
            return TimetableSemester(elem.text(), elem.attr("value"))
        }

        /**
         * Accepts a response document from https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0
         * *the initial request*
         */
        fun findCurrentWeek(doc: Document): TimetableWeek{
            val elem = doc.body().getElementById("prm1_id").nextElementSibling().select("option[selected]")
            return TimetableWeek(elem.text(), elem.attr("value"))
        }

        /**
         * Accepts a response document from https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0
         * *the initial request*
         */
        fun getAllSemesters(doc: Document): List<TimetableSemester>{
            val elem = doc.body().getElementById("prm1_id")
            val startList = mutableListOf<TimetableSemester>()
            elem.children().forEach {
                startList.add(TimetableSemester(it.text(), it.attr("value")))
            }
            return startList
        }

        /**
         * Accepts either a response document from https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0
         * or from https://uais.cr.ktu.lt/ktuis/TVARKARASTIS.kalend0
         */
        fun getAllWeeks(doc: Document): List<TimetableWeek>{
            val prm1 = doc.body().getElementById("prm1_id")
            val elem = prm1.nextElementSibling()
            val startList = mutableListOf<TimetableWeek>()
            elem.children().forEach {
                startList.add(TimetableWeek(it.text(), it.attr("value")))
            }
            return startList
        }

        /**
         *
         * Accepts a response document from https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0
         * *the initial request*
         *
         * The request to get a specific week schedule is a POST with content much like this:
         * `n=asm_id&v=119815&w=1&data_nuo=2018.09.03`
         * where `n` and `v` content is stored in a hidden input field
         *
         * The hidden inputs are stored in the form like this:
         * <form id="frm_sbl_id" style="display:inline;">
         *     <input type="hidden" name="n" value="asm_id">
         *     <input type="hidden" name="v" value="119815">
         *     <input type="hidden" name="w" value="1">
         *     <input type="hidden" name="data_nuo" value="2019.03.04">
         * </form>
         */
        fun findVandN(doc: Document): Pair<String, String>{
            val hiddenInputContainer = doc.body().getElementById("frm_sbl_id")
            val vInput = hiddenInputContainer.children().find { it.attr("name") == "v" }!!
            val nInput = hiddenInputContainer.children().find { it.attr("name") == "n" }!!
            return Pair(vInput.attr("value"), nInput.attr("value"))
        }
    }
}