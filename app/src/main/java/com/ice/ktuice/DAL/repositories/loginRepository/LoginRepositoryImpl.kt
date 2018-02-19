package com.ice.ktuice.DAL.repositories.loginRepository

import com.ice.ktuice.DAL.repositories.BaseRepository
import com.ice.ktuice.models.LoginModel
import io.realm.Realm

/**
 * Created by Andrius on 1/30/2018.
 * The default loginModel storage for realm
 */
class LoginRepositoryImpl(): BaseRepository<LoginModel>(), LoginRepository {
    override fun getWhere(key: String, value: String): LoginModel? {
        val rl = Realm.getDefaultInstance()
        val rlu = where<LoginModel>(rl).equalTo(key, value).findFirst() ?: return null
        return rlu
    }

    override fun getByLogin(username: String, password: String): LoginModel? {
        val rl = Realm.getDefaultInstance()
        val rlu = where<LoginModel>(rl).equalTo("username", username).equalTo("password", password).findFirst() ?: return null
        return rlu
    }

    override fun getByStudCode(code: String): LoginModel? {
        val rl = Realm.getDefaultInstance()
        val rlu = where<LoginModel>(rl).equalTo("studentId", code).findFirst() ?: return null
        return rlu
    }

    override fun createOrUpdate(loginModel: LoginModel) {
        println("Creating or updating login model:"+ loginModel.studentId)
        val rl = Realm.getDefaultInstance()
        rl.use { realm ->
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(loginModel)
            realm.commitTransaction()
            realm.close()
        }
    }

}