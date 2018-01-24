package com.ice.ktuice.DB.entities

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.RealmClass

/**
 * Created by Andrius on 1/24/2018.
 * Stores the cookie map content in the database
 */
@RealmClass
open class RlCookie(): RealmObject(){

    constructor(k:String, c:String): this(){
        key = k
        content = c
    }

    open var key: String = ""
    open var content:String = ""

    @LinkingObjects("cookies")
    open val owners: RealmResults<RlUserModel>? = null
}