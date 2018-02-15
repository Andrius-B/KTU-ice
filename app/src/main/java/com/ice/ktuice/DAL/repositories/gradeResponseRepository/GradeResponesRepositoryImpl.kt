package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.scraper.models.YearGradesModel
import com.ice.ktuice.scraper.models.YearModel
import com.ice.ktuice.scraper.models.responses.GradeResponseModel
import io.realm.Realm

/**
 * Created by Andrius on 2/1/2018.
 */
class GradeResponesRepositoryImpl{

    fun getByYearModel(studCode:String, yearModel: YearModel): YearGradesModel? {
        val realm = Realm.getDefaultInstance()
        val dbResponse = realm.where(YearGradesModel::class.java)
                                    .equalTo("responseMetadata.yearModel.id", yearModel.id)
                                    .equalTo("responseMetadata.yearModel.year", yearModel.year)
                                    .equalTo("responseMetadata.studentCode", studCode).findFirst() ?: return null

        return null
    }



    fun createOrUpdate(yearGrades: YearGradesModel) {
        val realm = Realm.getDefaultInstance()
        realm.use {
            realm.beginTransaction()
            realm.insertOrUpdate(yearGrades)
            realm.commitTransaction()
            realm.close()
        }
    }
}