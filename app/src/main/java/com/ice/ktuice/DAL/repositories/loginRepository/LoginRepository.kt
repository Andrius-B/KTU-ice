package com.ice.ktuice.DAL.repositories.loginRepository

import com.ice.ktuice.scraper.models.LoginModel
import io.realm.Realm

/**
 * Created by Andrius on 1/30/2018.
 */
interface LoginRepository {
    fun getByStudCode(code: String, rl: Realm): LoginModel?

    fun getWhere(key:String, value:String, rl: Realm): LoginModel?

    fun getByLogin(username:String, password:String, rl: Realm): LoginModel?

    fun createOrUpdate(loginModel: LoginModel, rl: Realm)
}