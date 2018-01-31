package com.ice.ktuice.DAL.repositories.loginRepository

import com.ice.ktuice.DAL.repositories.BaseRepository
import com.ice.ktuice.DB.entities.RlUserModel
import com.ice.ktuice.scraper.models.LoginModel
import io.realm.Realm
import java.util.*

/**
 * Created by Andrius on 1/30/2018.
 * The default loginModel storage for realm
 * TODO remove realm arguments, move to injection
 */
class LoginRepositoryImpl(): BaseRepository<RlUserModel>(), LoginRepository {
    override fun getWhere(key: String, value: String, rl: Realm): LoginModel? {
        val rlu = where<RlUserModel>(rl).equalTo(key, value).findFirst() ?: return null
        return RlUserModel.toLoginModel(rlu)
    }

    override fun getByLogin(username: String, password: String, rl: Realm): LoginModel? {
        val rlu = where<RlUserModel>(rl).equalTo("username", username).equalTo("password", password).findFirst() ?: return null
        return RlUserModel.toLoginModel(rlu)
    }

    override fun getByStudCode(code: String, rl: Realm): LoginModel? {
        val rlu = where<RlUserModel>(rl).equalTo("studId", code).findFirst() ?: return null
        return RlUserModel.toLoginModel(rlu)
    }

    override fun createOrUpdate(loginModel: LoginModel, rl: Realm) {
        val rlUser = RlUserModel.from(loginModel)!!
        rl.use { realm ->
            realm.beginTransaction()
            val rlu: RlUserModel
            val dbUser = realm.where(RlUserModel::class.java).equalTo("studId", rlUser.studId).findFirst()
            rlu = if(dbUser == null){
                val newUser = realm.createObject(RlUserModel::class.java, UUID.randomUUID().toString())
                newUser // if no user with the same vidko, create a new one
            }else dbUser // if there already is an existing user - update that

            rlu.set(rlUser)
            realm.commitTransaction()
            realm.close()
        }
    }

}