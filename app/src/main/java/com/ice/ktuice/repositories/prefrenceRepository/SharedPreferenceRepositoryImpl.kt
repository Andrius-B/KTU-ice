package com.ice.ktuice.repositories.prefrenceRepository

import android.content.Context
import com.ice.ktuice.R
import com.ice.ktuice.repositories.prefrenceRepository.PreferenceRepository

/**
 * Created by Andrius on 1/31/2018.
 */
class SharedPreferenceRepositoryImpl(private val context: Context): PreferenceRepository {

    override fun getValue(key: String, default: String): String {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE)
        return sharedPref.getString(key, default)!!
    }

    override fun getValue(key: String)
        = getValue(key, "")

    override fun setValue(key: String, value: String) {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString(key, value)
            apply()
        }
    }

    override fun getValue(keyResId: Int): String {
        val key = context.getString(keyResId)
        return  getValue(key)
    }

    override fun setValue(keyResId: Int, value: String) {
        val key = context.getString(keyResId)
        return setValue(key, value)
    }

}