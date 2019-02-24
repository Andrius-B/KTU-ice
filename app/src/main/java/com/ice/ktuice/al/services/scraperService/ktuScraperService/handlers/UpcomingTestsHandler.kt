package com.ice.ktuice.al.services.scraperService.ktuScraperService.handlers

import com.ice.ktuice.models.LoginModel
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * This handler is responsible for fetching upcoming tests
 */
class UpcomingTestsHandler{
    companion object {
        fun getUpcoming(login: LoginModel){
            val url = String.format("https://uais.cr.ktu.lt/ktuis/TVARKARASTIS.kalend0")
            val response = Jsoup.connect(url)
                    .cookies(login.getCookieMap())
                    .method(Connection.Method.POST)
                    .execute()
            val doc = response.parse()
            val elems = doc.body().allElements
            print(elems[elems.lastIndex-2].html())
        }


        fun getSemesterBeginings(login: LoginModel){
            val url = String.format("https://uais.cr.ktu.lt/ktuis/TV_STUD.stud_kal_w0")
            val response = Jsoup.connect(url)
                    .cookies(login.getCookieMap())
                    .method(Connection.Method.GET)
                    .execute()
            val doc = response.parse()
            val semesterStartList = getSemesterStartDates(doc)
            println("Semesters start at:")
            semesterStartList.forEach{ println(it) }

            val weekStartList = getWeekStartDatesForSemester(doc)
            println("Weeks start at:")
            weekStartList.forEach{ println(it) }
        }

        fun getSemesterStartDates(doc: Document): List<String>{
            val elem = doc.body().getElementById("prm1_id")
            val startList = mutableListOf<String>()
            elem.children().forEach {
                startList.add(it.attr("value"))
            }
            return startList
        }

        fun getWeekStartDatesForSemester(doc: Document): List<String>{
            val prm1 = doc.body().getElementById("prm1_id")
            val elem = prm1.nextElementSibling()
            val startList = mutableListOf<String>()
            elem.children().forEach {
                startList.add(it.attr("value"))
            }
            return startList
        }
    }
}