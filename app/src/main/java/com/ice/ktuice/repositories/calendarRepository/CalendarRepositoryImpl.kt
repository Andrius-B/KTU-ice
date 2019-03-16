package com.ice.ktuice.repositories.calendarRepository

import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import io.realm.Realm
import io.realm.RealmModel


/**
 * Created by Andrius on 3/10/2018.
 * The default calendar repository
 */
class CalendarRepositoryImpl: CalendarRepository {
    override fun getByStudCode(studCode: String): CalendarModel? {
        val realm = Realm.getDefaultInstance()
        val query = realm.where(CalendarModel::class.java)
                .equalTo("studCode", studCode)
        return query.findFirst()
    }

    override fun createOrUpdate(calendarModel: CalendarModel) {
        val realm = Realm.getDefaultInstance()
            realm.use {
                realm.beginTransaction()
                realm.insertOrUpdate(calendarModel as RealmModel)
                realm.commitTransaction()
                realm.close()
            }
    }

}