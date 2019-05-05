package com.ice.ktuice.repositories.loginRepository

import com.ice.ktuice.models.LoginModel

/**
 * Created by Andrius on 1/30/2018.
 * used for storing logins on the device
 */
interface LoginRepository {
    fun getByStudCode(code: String): LoginModel?

    fun getWhere(key:String, value:String): LoginModel?

    fun getByLogin(username:String, password:String): LoginModel?

    fun createOrUpdate(loginModel: LoginModel)
}