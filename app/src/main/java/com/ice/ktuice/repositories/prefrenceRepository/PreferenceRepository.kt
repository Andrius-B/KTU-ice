package com.ice.ktuice.repositories.prefrenceRepository

/**
 * Created by Andrius on 1/31/2018.
 */
interface PreferenceRepository {
    /**
     * @return a value that corresponds to the key specified. Blank if not found
     */
    fun getValue(key: String):String
    fun getValue(key: String, default: String):String
    fun getValue(keyResId: Int): String
    fun setValue(key: String, value: String)
    fun setValue(keyResId: Int, value: String)
}