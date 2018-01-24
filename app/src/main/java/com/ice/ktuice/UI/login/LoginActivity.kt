package com.ice.ktuice.UI.login

import android.content.Context
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
import org.jetbrains.anko.getStackTraceString
import java.util.*

/**
 * Created by Andrius on 1/24/2018.
 */
class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        RealmConfig.init(this) // init the db

        login_submit_button.setOnClickListener{
            val username = login_username_field.text.toString()
            val password = login_password_field.text.toString()
            doAsync(
            {
                println(it)
                println(it.getStackTraceString())
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
                this.activityUiThreadWithContext{
                    if(loginModel != null) {
                        val realm = Realm.getDefaultInstance()
                        val rlUser = RlUserModel.from(loginModel)

                        if (rlUser != null) {
                            println("RlUserCreated!")
                            realm.use { realm ->
                                realm.beginTransaction()
                                //val rlu = realm.createObject(RlUserModel::class.java, UUID.randomUUID().toString())
                                val rlu = realm.createObject(RlUserModel::class.java, UUID.randomUUID().toString())
                                println("Realm object created!")
                                rlu.set(rlUser)
                                println("Realm object set!")
                                realm.commitTransaction()
                                println("Realm transaction finished")
                            }
                        } else {
                            println("rlUser is null!")
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        setErrorDisplay("login is null!", true)
                        setLoadingVisible(false)
                    }
                }
            })
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