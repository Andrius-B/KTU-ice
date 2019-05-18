package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.base.exceptions.ServerErrorException
import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URLDecoder

class AgreeStage: Stage() {
    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        return executeCountingRetries(cookieJar, dataStore, 0)
    }

    private fun executeCountingRetries(cookieJar: HashMap<String, String>, dataStore: LoginDataStore, retries: Int): Int {
        val url = "https://login.ktu.lt/simplesaml/module.php/consent/getconsent.php?" +
                "StateId=${dataStore.stateId}&" +
                "yes=Yes%2C%20continue%0D%0A&" +
                "saveconsent=1"
        val request = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .cookies(cookieJar)
                .followRedirects(true)
                .execute()
        val parse = request.parse()
        println("Agree page:$parse")
        val inputList = parse.select("input")
        try {
            val stateId = inputList.first { it.attr("name") == "StateId" }.attr("value")
            if (stateId != dataStore.stateId) {
                dataStore.stateId = stateId
                // retry on StateId mismatch
                println("StateId miss-match")
                cookieJar.putAll(request.cookies())
                if(retries < 5) {
                    return executeCountingRetries(cookieJar, dataStore, retries)
                }else{
                    throw ServerErrorException("Log in could not complete successfully!")
                }
            }
        }catch (e: NoSuchElementException){
            // if the correct no-js version is fetched
        }

        cookieJar.putAll(request.cookies())
//        try{
//        dataStore.samlResponse = inputList.first { it.attr("name") == "SAMLResponse" }.attr("value")
//        dataStore.relayState = getRelayStateFromAuthState(dataStore.authState)
//        }catch (e: Exception){
//
//        }
        return request.statusCode()
    }

    private fun getRelayStateFromAuthState(authState: String): String {
        val start = authState.indexOf("RelayState")
        val end = authState.indexOf('&', start)
        val relayState = URLDecoder.decode(authState.substring(start, end), "UTF-8")
        return relayState.split('=')[1]
    }

}