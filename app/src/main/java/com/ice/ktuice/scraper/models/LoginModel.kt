package com.ice.ktuice.scraper.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class LoginModel(
        var authCookies: RealmList<Cookie> = RealmList(),
        var studentName: String = "",
        @PrimaryKey
        var studentId: String = "",
        var studentSemesters: RealmList<YearModel> = RealmList(),
        var username: String = "",
        var password: String = ""
):RealmObject(){
        /**
         * Utility function to change types:
         * @param cookies - cookie map from jsoup request
         * @return List of Realm objects containing keys and values
         */
        fun setCookieMap(cookies: Map<String, String>) { //remap the map of cookies to a list of key-value pairs
            authCookies = RealmList<Cookie>().apply {
                cookies.forEach({add(Cookie(it.key, it.value))})
            }
        }

        /**
         * Utility function to change types:
         * @param rlCookies - cookie list from realm
         * @return map of <String, String> pairs as per standard
         */
        fun getCookieMap():Map<String, String>{
            return mutableMapOf<String, String>().apply {
                authCookies.forEach({ put(it.key, it.content) })
            }
        }
}