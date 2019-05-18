package com.ice.ktuice.al.services.scrapers.login

import com.ice.ktuice.models.responses.LoginResponseModel

interface LoginService {
    /**
     * Gets authentication cookies
     * and general information about a student
     * @return Authentication cookies and user info
     */
    fun login(username: String, password: String): LoginResponseModel
}