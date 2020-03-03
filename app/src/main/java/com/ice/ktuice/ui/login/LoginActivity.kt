package com.ice.ktuice.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ice.ktuice.R
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.repositories.loginRepository.LoginRepository
import com.ice.ktuice.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

/**
 * Created by Andrius on 1/24/2018.
 */
class LoginActivity: AppCompatActivity(), IceLog {

    private val loginRepository: LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()
    private val scraperService: ScraperService by inject()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        info("Creating login activity!")
        val loggedInUserCode = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(loggedInUserCode.isNotBlank()){
            val login = loginRepository.getByStudCode(loggedInUserCode)
            if(login!= null){
                info("Launching with logged in user code:"+login.studentId)
                launchMainActivity()
            }
        }
        setContentView(R.layout.activity_login)

        login_submit_button.setOnClickListener{
            val username = login_username_field.text.toString()
            val password = login_password_field.text.toString()
            val loginFuture= loginRequest(username, password)

            GlobalScope.launch (Dispatchers.IO) {
                val loginModel = loginFuture.await()
                if (loginModel == null) {
                    setErrorDisplay(resources.getString(R.string.failed_login), true)
                    setLoadingVisible(false)
                } else {
                    launch (Dispatchers.Main) {
                        saveLoginToRealm(loginModel)
                        preferenceRepository.setValue(R.string.logged_in_user_code, loginModel.studentId)
                        info("login saved to database, launching main activity!")
                        launchMainActivity()
                    }
                }
            }
        }
    }

    private fun launchMainActivity(){
        runOnUiThread{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginRequest(username:String, password:String): Deferred<LoginModel?>{
            return GlobalScope.async {
                var loginModel: LoginModel? = null
                try {
                    setLoadingVisible(true)
                    val loginResponse = scraperService.login(username, password)
                    if(loginResponse.loginModel != null) {
                        setLoadingVisible(false)
                        loginModel = loginResponse.loginModel
                        info("Login successful! " + loginModel.studentName)
                    }
                }catch (e: Exception){
                    info("Exception while making the login requests!:$e")
                    setErrorDisplay(e.toString(), true)
                    info(Log.getStackTraceString(e))
                    setLoadingVisible(false)
                }
                loginModel
            }
    }


    private fun saveLoginToRealm(loginModel: LoginModel){
        loginRepository.createOrUpdate(loginModel)
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