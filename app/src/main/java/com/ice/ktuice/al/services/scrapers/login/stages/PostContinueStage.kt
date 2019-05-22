package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import com.ice.ktuice.al.services.scrapers.login.LoginUtil
import org.jsoup.Connection
import org.jsoup.Jsoup

class PostContinueStage: Stage() {
    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        val url = "https://uais.cr.ktu.lt/shibboleth/SAML2/POST"
        val request = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data(mapOf(
                        "SAMLResponse" to dataStore.samlResponse,
                        "RelayState" to dataStore.relayState
                ))
                .headers(LoginUtil.getAdditionalHeaders())
                .execute()
        cookieJar.putAll(request.cookies())
        return request.statusCode()
    }
}