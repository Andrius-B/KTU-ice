package com.ice.ktuice.al.settings

import android.content.Context
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import io.realm.log.RealmLog.warn
import org.jetbrains.anko.warn
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Simple persistent settings stored in the preference repository
 */
class AppSettingsPreferencesImpl(private val context: Context): AppSettings, KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()


    override var currentThemePos: Int
        get(){
            try{
                return preferenceRepository.getValue(context.getString(R.string.currently_selected_theme_position)).toInt()
            }catch (e: NumberFormatException){
                warn("Wrong theme set! Resetting..")
                preferenceRepository.setValue(context.getString(R.string.currently_selected_theme_position), "0")
            }
            return 0
        }
        set(value){
            preferenceRepository.setValue(context.getString(R.string.currently_selected_theme_position), value.toString())
        }

    override var gradeNotificationsEnabled: Boolean
        get(){
            try{
                return preferenceRepository.getValue(context.getString(R.string.grade_notifications_enabled)).toBoolean()
            }catch (e: NumberFormatException){
                warn("Wrong preference variable set! Resetting..")
                preferenceRepository.setValue(context.getString(R.string.grade_notifications_enabled), true.toString())
            }
            return true
        }
        set(value){
            preferenceRepository.setValue(context.getString(R.string.grade_notifications_enabled), value.toString())
        }

    override var networkingEnabled: Boolean
        get(){
            try{
                return preferenceRepository.getValue(context.getString(R.string.networking_enabled_key)).toBoolean()
            }catch (e: NumberFormatException){
                warn("Wrong preferenceVariable set! Resetting..")
                preferenceRepository.setValue(context.getString(R.string.networking_enabled_key), true.toString())
            }
            return true
        }
        set(value){
            preferenceRepository.setValue(context.getString(R.string.networking_enabled_key), value.toString())
        }
}