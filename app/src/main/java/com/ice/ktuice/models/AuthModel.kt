package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.RealmClass

/**
 * The minimal set of information required to successfully log in and use the KTU ais website
 */
@RealmClass
open class AuthModel(
        var authCookies: RealmList<Cookie> = RealmList(),
        var username: String = "",
        var password: String = ""
        ): RealmObject() {
    /**
     * Utility function to change types:
     * @param cookies - cookie map from jsoup request
     * @return List of Realm objects containing keys and values
     */
    open fun setCookieMap(cookies: Map<String, String>) { //remap the map of cookies to a list of key-value pairs
        authCookies = RealmList<Cookie>().apply {
            cookies.forEach({add(Cookie(it.key, it.value))})
        }
    }

    /**
     * Utility function to get the cookies as a map for use in request
     * @return map of <String, String> pairs as per standard
     */
    open fun getCookieMap():Map<String, String>{
        return mutableMapOf<String, String>().apply {
            authCookies.forEach({ put(it.key, it.content) })
        }
    }

    @LinkingObjects("authModel")
    open val owners: RealmResults<LoginModel>? = null
}