package com.ice.ktuice.DAL.repositories.loginRepository

import com.ice.ktuice.scraper.models.LoginModel
import io.realm.Realm

/**
 * Created by Andrius on 1/30/2018.
 */
interface LoginRepository {
    fun getByStudCode(code: String): LoginModel?

    fun getWhere(key:String, value:String): LoginModel?

    fun getByLogin(username:String, password:String): LoginModel?

    fun createOrUpdate(loginModel: LoginModel)
}