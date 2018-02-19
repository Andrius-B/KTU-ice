package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * Created by Andrius on 2/1/2018.
 */
class YearGradesRepositoryImpl: YearGradesRepository {

    override fun getByStudCode(studCode:String): RealmResults<YearGradesModel> {
        val realm = Realm.getDefaultInstance()
        return realm.where(YearGradesModel::class.java)
                .equalTo("studCode", studCode)
                .sort("dateStamp", Sort.DESCENDING)
                .distinctValues("yearStr")
                .findAll()
    }



    override fun createOrUpdate(yearGradesModel: YearGradesModel) {
        val realm = Realm.getDefaultInstance()

        /**
         * For this version, testing out the ability of our database to only maintain two copies of
         * a single yearGradesModel, to keep other filtering simpler.
         */
        val dbModels = getByStudCode(yearGradesModel.studCode)
        dbModels.where()
                .equalTo("_year.year", yearGradesModel.year.year)
                .equalTo("_year.id", yearGradesModel.year.id)
                .lessThan("dateStamp", yearGradesModel.dateStamp)
                .sort("dateStamp", Sort.DESCENDING)
                .findAll()

        realm.use {
            realm.beginTransaction()
            /*if(dbModels.size >= 2){
                dbModels.deleteFirstFromRealm()
            }*/
            realm.insertOrUpdate(yearGradesModel)
            realm.commitTransaction()
            realm.close()
        }
    }
}