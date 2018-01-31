package com.ice.ktuice.DAL.repositories

import io.realm.Realm
import io.realm.RealmObject

/**
 * Created by Andrius on 1/30/2018.
 * @param realm - injected default instance of realm
 */
abstract class BaseRepository<T: RealmObject>() {
    inline fun <reified T: RealmObject>where(realm: Realm) = realm.where(T::class.java)
}