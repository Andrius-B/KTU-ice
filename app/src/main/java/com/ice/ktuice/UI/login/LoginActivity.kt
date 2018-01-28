package com.ice.ktuice.UI.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ice.ktuice.DB.RealmConfig
import com.ice.ktuice.DB.entities.RlUserModel
import com.ice.ktuice.R
import com.ice.ktuice.UI.main.MainActivity
import com.ice.ktuice.scraper.handlers.LoginHandler
import com.ice.ktuice.scraper.models.LoginModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityUiThreadWithContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.getStackTraceString
import java.util.*
import java.util.concurrent.Future

/**
 * Created by Andrius on 1/24/2018.
 * TODO refactor the login system to a more robust system
 */
class LoginActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        RealmConfig.init(this) // init the db

        login_submit_button.setOnClickListener{
            val username = login_username_field.text.toString()
            val password = login_password_field.text.toString()
            val loginFuture= loginRequest(username, password)

            doAsync {
                val loginModel = loginFuture.get()
                if (loginModel == null) {
                    setErrorDisplay("login is null!", true)
                    setLoadingVisible(false)
                } else {
                    saveLoginToRealm(loginModel)
                    activityUiThreadWithContext {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("vidko", loginModel.studentId)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun loginRequest(username:String, password:String): Future<LoginModel?>{
        return doAsyncResult(
                {
                    println(it)
                    println(it.getStackTraceString())
                    runOnUiThread{
                        setErrorDisplay(it.toString(), true)
                        setLoadingVisible(false)
                    }
                },
                {
                    /**
                     * Login testing
                     */
                    var loginModel: LoginModel? = null
                    try {
                        setLoadingVisible(true)
                        val loginResponse = LoginHandler().getAuthCookies(username, password)
                        if(loginResponse.loginModel != null) {
                            setLoadingVisible(false)
                            loginModel = loginResponse.loginModel
                            println("Login successful! " + loginModel.studentName)
                        }
                    }catch (e: Exception){
                        println("Exception while making the login requests!:"+e)
                        setErrorDisplay(e.toString(), true)
                        println(e.getStackTraceString())
                        setLoadingVisible(false)
                    }
                    return@doAsyncResult loginModel
                })
    }


    private fun saveLoginToRealm(loginModel: LoginModel){
        val realm = Realm.getDefaultInstance()
        val rlUser = RlUserModel.from(loginModel)

        if (rlUser != null) {
            //println("RlUserCreated!")
            realm.use { rl ->
                rl.beginTransaction()
                val rlu: RlUserModel?
                val dbUser = rl.where(RlUserModel::class.java).equalTo("studId", rlUser.studId).findFirst()
                if(dbUser == null){
                    val newUser = rl.createObject(RlUserModel::class.java, UUID.randomUUID().toString())
                    rlu = newUser // if no user with the same vidko, create a new one
                }else rlu = dbUser // if there already is an existing user - update that

                println("Realm object created!")
                rlu?.set(rlUser)
                println("Realm object set!")
                rl.commitTransaction()
                println("Realm transaction finished")
            }
        } else {
            println("rlUser is null!")
        }
    }

    private fun setLoadingVisible(visible:Boolean){
        runOnUiThread {
            if(visible){
                login_submit_button.isEnabled = false
                login_spinner.visibility = View.VISIBLE
            }else{
                login_submit_button.isEnabled = true
                login_spinner.visibility = View.GONE
            }
        }
    }

    private fun setErrorDisplay(errorText:String, visible:Boolean){
        runOnUiThread {
            login_error_text.text = errorText
            if(visible){
                login_error_container.visibility = View.VISIBLE
            }else{
                login_error_container.visibility = View.GONE
            }
        }
    }
}