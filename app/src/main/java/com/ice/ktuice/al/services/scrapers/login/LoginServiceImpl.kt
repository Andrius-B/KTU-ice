package com.ice.ktuice.al.services.scrapers.login

import com.ice.ktuice.al.services.scrapers.login.stages.*
import com.ice.ktuice.models.AuthModel
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponseModel
import io.realm.RealmList
import org.jsoup.Connection
import org.jsoup.Jsoup

class LoginServiceImpl: LoginService {
    private val cookieJar = HashMap<String, String>()
    private val dataStore = LoginDataStore()

    private val stageSequence = listOf(
            PreLoginStage(),
            AutoLoginStage(),
            LoginUserPassStage(),
            AgreeStage(),
            PostContinueStage()
    )

    override fun login(username: String, password: String): LoginModel {
        dataStore.userName = username
        dataStore.password = password

        for(stage in stageSequence){
            stage.execute(cookieJar, dataStore)
        }
        val authModel = AuthModel(
                RealmList(),
                username,
                password
        )
        authModel.setCookieMap(cookieJar)
        return getLoginModel(authModel)
    }

    fun getLoginModel(authModel: AuthModel): LoginModel {
        val url = "https://uais.cr.ktu.lt/ktuis/vs.ind_planas"
        val request = Jsoup.connect(url)
                .cookies(authModel.getCookieMap())
                .method(Connection.Method.GET)
                .headers(LoginUtil.getAdditionalHeaders())
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
        return LoginModel(
                studentName = studentName,
                studentId = studentId,
                studentSemesters = RealmList<YearModel>().apply { addAll(studyList) },
                authModel = authModel
        )
    }
}