package com.ice.ktuice.al.services.scrapers.login

import com.ice.ktuice.al.services.scrapers.login.stages.*
import com.ice.ktuice.models.responses.LoginResponseModel

class LoginServiceImpl: LoginService {
    private val cookieJar = HashMap<String, String>()
    private val dataStore = LoginDataStore()

    private val stageSequence = listOf(
            PreLoginStage(),
            AutoLoginStage(),
            LoginUserPassStage(),
//            AgreeStage(),
            PostContinueStage()
    )

    override fun login(username: String, password: String): LoginResponseModel {
        dataStore.userName = username
        dataStore.password = password

        for(stage in stageSequence){
            stage.execute(cookieJar, dataStore)
        }
        return LoginResponseModel(null, 500)
    }
}