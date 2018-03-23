package com.ice.ktuice.scraperService.handlers

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponse
import io.realm.RealmList
import org.jsoup.Connection
import org.jsoup.Jsoup

class LoginHandler: BaseHandler() {

    fun getAuthCookies(username: String, password: String): LoginResponse {
        val autoLogin = getAutoLogin()
        val postLogin = postLogin(username, password, autoLogin)
        if (postLogin.cookies != null) {
            val agreeLogin = getAgree(postLogin)
            val postContinue = postContinue(agreeLogin)
            return getInfo(postContinue, username, password)
        }
        // if there are no cookies returned from postLogin,
        // assume not authorized!
        return LoginResponse(null, 401)
    }

    private class AutoLoginResponse(
            val authState: String,
            val cookies: Map<String, String>,
            val responseCode: Int
    )

    private class PostLoginResponse(
            val stateId: String?,
            val cookies: Map<String, String>?,
            val responseCode: Int
    )

    private class AgreeResponse(
            val samlResponse: String,
            val relayState: String,
            val responseCode: Int
    )
    private class AuthResponse(
            val authCookies: Map<String, String>,
            val responseCode: Int
    )

    /**
     * This is the default login page, where if
     * the connecting client has appropriate shib session cookies,
     * it gives a STUDCOOKIE, both of which are needed to authenticate a user
     * @return AuthState and cookies
     */
    private fun getAutoLogin(): AutoLoginResponse {
        val url = "https://uais.cr.ktu.lt/ktuis/studautologin"
        val request = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute()
        val parse = request.parse()
        val select = parse.select("input[name=\"AuthState\"]")
        val attr = select[0].attr("value")
        return AutoLoginResponse(attr, request.cookies(), request.statusCode())
    }

    private fun postLogin(
            username: String,
            password: String,
            autoLoginResponse: AutoLoginResponse): PostLoginResponse {

        val url = "https://login.ktu.lt/simplesaml/module.php/core/loginuserpass.php"
        val request = Jsoup.connect(url)
                .cookies(autoLoginResponse.cookies)
                .data(mapOf(
                        "username" to username,
                        "password" to password,
                        "AuthState" to autoLoginResponse.authState
                ))
                .method(Connection.Method.POST)
                .execute()
        val parse = request.parse()
        val inputList = parse.select("input")
        val filteredInputList = inputList.firstOrNull { it.attr("name") == "StateId" }
        if (filteredInputList != null) {
            val stateId = filteredInputList.attr("value")
            return PostLoginResponse(stateId, request.cookies() + autoLoginResponse.cookies, request.statusCode())
        }
        return PostLoginResponse(null, null, request.statusCode())
    }

    private fun getAgree(postLoginResponse: PostLoginResponse): AgreeResponse {
        val url = "https://login.ktu.lt/simplesaml/module.php/consent/getconsent.php?" +
                "StateId=${postLoginResponse.stateId}&" +
                "yes=Yes%2C%20continue%0D%0A&" +
                "saveconsent=1"
        val request = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .cookies(postLoginResponse.cookies)
                .execute()

        val parse = request.parse()
        val inputList = parse.select("input")
        val samlResponse = inputList.first { it.attr("name") == "SAMLResponse" }.attr("value")
        val relayState = inputList.first { it.attr("name") == "RelayState" }.attr("value")
        return AgreeResponse(samlResponse, relayState, request.statusCode())
    }

    private fun postContinue(agreeResponse: AgreeResponse): AuthResponse {
        val url = "https://uais.cr.ktu.lt/shibboleth/SAML2/POST"
        val request = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data(mapOf(
                        "SAMLResponse" to agreeResponse.samlResponse,
                        "RelayState" to agreeResponse.relayState
                ))
                .execute()
        return AuthResponse(request.cookies(), request.statusCode())
    }

    private fun getInfo(authResponse: AuthResponse, username: String, password: String): LoginResponse {
        val url = "https://uais.cr.ktu.lt/ktuis/vs.ind_planas"
        val request = Jsoup.connect(url)
                .cookies(authResponse.authCookies)
                .method(Connection.Method.GET)
                .execute()

        request.charset("windows-1257")
        val parse = request.parse()
        val nameItemText = parse.select("#ais_lang_link_lt").parents().first().text()
        val studentId = nameItemText.split(' ')[0].trim()
        val studentName = nameItemText.split(' ')[1].trim()
        val studyList = mutableListOf<YearModel>().apply {
            val studyYears = parse.select(".ind-lst.unstyled > li > a")
            val yearRegex = "plano_metai=([0-9]+)".toRegex()
            val idRegex = "p_stud_id=([0-9]+)".toRegex()
            studyYears.forEach { yearHtml ->
                val link = yearHtml.attr("href")
                val id = idRegex.find(link)!!.groups[1]!!.value
                val year = yearRegex.find(link)!!.groups[1]!!.value
                add(YearModel(id, year))
            }
        }
        val loginModel = LoginModel(
                studentName = studentName,
                studentId = studentId,
                studentSemesters = RealmList<YearModel>().apply { addAll(studyList) },
                username = username,
                password = password
        )
        loginModel.setCookieMap(authResponse.authCookies)
        return LoginResponse(loginModel, request.statusCode())
    }
}