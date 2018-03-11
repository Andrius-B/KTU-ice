package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.Sort

/**
 * Created by Andrius on 2/1/2018.
 */
class YearGradesRepositoryImpl: YearGradesRepository {

    override fun getByStudCode(studCode:String): YearGradesCollectionModel? {
        val realm = Realm.getDefaultInstance()
        return realm.where(YearGradesCollectionModel::class.java)
                        .equalTo("studentId", studCode)
                        .findFirst()
    }

    override fun createOrUpdate(yearGradesModel: YearGradesCollectionModel) {
        val realm = Realm.getDefaultInstance()
        realm.use {
            realm.beginTransaction()
            realm.insertOrUpdate(yearGradesModel as RealmModel)
            realm.commitTransaction()
            realm.close()
        }
    }

}