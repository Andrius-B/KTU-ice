package com.ice.ktuice.models

import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.collections.joinToString;

@RealmClass
open class LoginModel(
        var studentName: String = "",
        @PrimaryKey
        var studentId: String = "",
        var studentSemesters: RealmList<YearModel> = RealmList(),
        private var authModel: AuthModel = AuthModel(RealmList(), "", "")
):RealmObject(){


    var username: String
        get() = authModel.username
        set(value) {authModel.username = value}

    var password: String
        get() = authModel.password
        set(value) {authModel.password = value}

    var authCookies: RealmList<Cookie>
        get() = authModel.authCookies
        set(value) {authModel.authCookies = value}

    fun setCookieMap(cookies: Map<String, String>)
        = authModel.setCookieMap(cookies)

    fun getCookieMap():Map<String, String>
        = authModel.getCookieMap()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCookieAsString(): String {
        val cookies = getCookieMap().entries.stream().map {
            it.key + "=" + it.value
        }.collect(Collectors.toList())
        return cookies.joinToString(separator = "; ")
    }
}