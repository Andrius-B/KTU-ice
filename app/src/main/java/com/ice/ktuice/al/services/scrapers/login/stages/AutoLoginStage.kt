package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import org.jsoup.Connection
import org.jsoup.Jsoup


class AutoLoginStage: Stage() {
    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        val url = "https://uais.cr.ktu.lt/ktuis/studautologin"
        val request = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .followRedirects(true)
                .execute()
        val parse = request.parse()
        val select = parse.select("input[name=\"AuthState\"]")
        val attr = select[0].attr("value")
        cookieJar.putAll(request.cookies())
        dataStore.authState = attr
        println("AutoLoginStage:$parse")
        return request.statusCode()
    }
}