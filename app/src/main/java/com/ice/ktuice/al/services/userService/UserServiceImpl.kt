package com.ice.ktuice.al.services.userService

import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.scraperService.ScraperService
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/24/2018.
 */
class UserServiceImpl: UserService, KoinComponent{
    private val preferenceRepository: PreferenceRepository by inject()
    private val loginRepository: LoginRepository by inject()
    private val scraperService: ScraperService by inject()

    override fun getLoginForCurrentUser(): LoginModel {
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if (requestedStudentId.isBlank()) {
            println("StudentCode not found, quitting!")
            throw NullPointerException("Student code is not found, can not find a logged in user!")
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId)
        if (loginModel == null) {
            println("Login model is null!")
            throw NullPointerException("Login model for the requested code is null, can not get a logged in user!")
        }else{
            return loginModel
        }
    }

    /**
     * Refreshes the cookies of the currently logged in user and returns the new login model with the cookies
     * @throws null exception if something goes wrong with the login requests.
     */
    override fun refreshLoginCookies(): LoginModel? {
        val loginModel = getLoginForCurrentUser()
        val newLoginModelResponse = scraperService.login(loginModel.username, loginModel.password)
        val newLoginModel = newLoginModelResponse.loginModel
        loginModel.authCookies.clear()
        loginModel.authCookies.addAll(newLoginModel?.authCookies!!)
        return newLoginModel
    }
}