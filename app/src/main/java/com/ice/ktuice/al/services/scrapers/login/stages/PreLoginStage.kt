package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import com.ice.ktuice.al.services.scrapers.login.LoginUtil
import org.jsoup.Connection
import org.jsoup.Jsoup

class PreLoginStage: Stage() {
    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        val url = "https://uais.cr.ktu.lt/ktuis/stp_prisijungimas"
        val request = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .headers(LoginUtil.getAdditionalHeaders())
                .execute()
        dataStore.setKeepingOld("PreLoginPage", request.body())
        cookieJar.putAll(request.cookies())
        return request.statusCode()
    }
}