package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import org.jsoup.Connection
import org.jsoup.Jsoup

class PostContinueStage: Stage() {
    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        val url = "https://uais.cr.ktu.lt/shibboleth/SAML2/POST"
        println("Hitting post continue with: \nSAMLResponse:${dataStore.samlResponse}\nRealyState:${dataStore.relayState}")
        val request = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data(mapOf(
                        "SAMLResponse" to dataStore.samlResponse,
                        "RelayState" to dataStore.relayState
                ))
                .execute()
        println("PostContinuePage\n" + request.body())
        cookieJar.putAll(request.cookies())
        return request.statusCode()
    }
}