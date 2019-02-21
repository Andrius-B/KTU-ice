package com.ice.ktuice.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.al.logger.FileLogReader
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.ui.adapters.FragmentAdapter
import com.ice.ktuice.ui.login.LoginActivity
import io.realm.log.RealmLog.warn
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.warn
import org.koin.android.ext.android.inject



class MainActivity : AppCompatActivity(), IceLog {

    /**
     * This preference is set in the settigns fragment and defines the number of the currently selected theme:
     * 0 (default) light
     * 1 dark
     */
    var currentTheme: Int
        get(){
            try{
                return preferenceRepository.getValue(this.getString(R.string.currently_selected_theme_position)).toInt()
            }catch (e: NumberFormatException){
                warn("Wrong theme set! Resetting..")
                preferenceRepository.setValue(this.getString(R.string.currently_selected_theme_position), "0")
            }
            return 0
        }
        set(value){
            preferenceRepository.setValue(this.getString(R.string.currently_selected_theme_position), value.toString())
        }

    private val userService: UserService by inject()
    private val preferenceRepository:PreferenceRepository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(currentTheme == 0){
            this.setTheme(R.style.iceTheme)
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO)
        }else if(currentTheme == 1){
            this.setTheme(R.style.darkIceTheme)
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES)
        }
        try{
            /**
             * If the user is not yet logged in,
             * this will throw a null reference exception
             */
            userService.getLoginForCurrentUser()
            val preferredTab = preferenceRepository.getValue(R.string.currently_selected_tab_id)
            setContentView(R.layout.activity_main)
            main_activity_view_pager.adapter = FragmentAdapter(this.supportFragmentManager, this)
            try {
                val preferredTabInt = preferredTab.toInt()
                main_activity_view_pager.currentItem = preferredTabInt
            }catch (e: Exception){}
        }catch (e: NullPointerException){
            launchLoginActivity()
        }
    }

    private fun launchLoginActivity(){
        runOnUiThread{
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }
    }
}
