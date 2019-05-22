package com.ice.ktuice.models

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.RealmClass

/**
 * Created by Andrius on 1/24/2018.
 * Stores the cookie map content in the database
 */
@RealmClass
open class Cookie(): RealmObject(){

    constructor(k:String, c:String): this(){
        key = k
        content = c
    }

    var key: String = ""
    var content:String = ""

    @LinkingObjects("authCookies")
    open val owners: RealmResults<AuthModel>? = null
}