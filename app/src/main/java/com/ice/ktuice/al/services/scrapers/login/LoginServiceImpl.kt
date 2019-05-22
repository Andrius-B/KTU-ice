package com.ice.ktuice.al.services.scrapers.login

import com.ice.ktuice.al.services.scrapers.login.stages.*
import com.ice.ktuice.models.AuthModel
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.responses.LoginResponseModel
import io.realm.RealmList

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

    override fun login(username: String, password: String): AuthModel {
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
        return authModel
    }
}