package com.ice.ktuice.DAL.repositories

import io.realm.Realm
import io.realm.RealmObject

abstract class BaseRepository<T: RealmObject> {
    inline fun <reified T: RealmObject>where(realm: Realm) = realm.where(T::class.java)!!
}