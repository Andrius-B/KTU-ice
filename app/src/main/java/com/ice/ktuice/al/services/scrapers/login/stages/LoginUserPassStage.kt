package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore
import com.ice.ktuice.al.services.scrapers.login.LoginUtil
import org.jsoup.Connection
import org.jsoup.Jsoup

class LoginUserPassStage: Stage() {

    override fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int {
        val url = "https://login.ktu.lt/simplesaml/module.php/core/loginuserpass.php"
        val request = Jsoup.connect(url)
                .cookies(cookieJar)
                .data(mapOf(
                        "username" to dataStore.userName,
                        "password" to dataStore.password,
                        "AuthState" to dataStore.authState
                ))
                .headers(LoginUtil.getAdditionalHeaders())
                .method(Connection.Method.POST)
                .execute()
        val parse = request.parse()
        val parsedQueryArguments = LoginUtil.splitQuery(parse.baseUri())
        cookieJar.putAll(request.cookies())
        dataStore.stateId = parsedQueryArguments.getValue("StateId")
        val inputList = parse.select("input")
        try{
            // these can either be given here or in the AgreeStage
            dataStore.samlResponse = inputList.first { it.attr("name") == "SAMLResponse" }.attr("value")
            dataStore.relayState = inputList.first { it.attr("name") == "RelayState" }.attr("value")
        }catch (e: NoSuchElementException){
            println(e.stackTrace)
        }
        return request.statusCode()
    }
}