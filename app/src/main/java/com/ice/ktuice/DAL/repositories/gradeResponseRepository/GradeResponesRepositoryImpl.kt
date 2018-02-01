package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.DB.entities.RlGradeResponseModel
import com.ice.ktuice.DB.entities.RlUserModel
import com.ice.ktuice.DB.entities.RlYearModel
import com.ice.ktuice.scraper.models.GradeResponseModel
import com.ice.ktuice.scraper.models.ResponseMetadataModel
import com.ice.ktuice.scraper.models.YearModel
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

/**
 * Created by Andrius on 2/1/2018.
 */
class GradeResponesRepositoryImpl: GradeResponseRepository {

    override fun getByYearModel(studCode:String, yearModel: YearModel, realm: Realm): GradeResponseModel? {
        val dbResponse = realm.where(RlGradeResponseModel::class.java)
                                    .equalTo("responseMetadata.yearModel.id", yearModel.id)
                                    .equalTo("responseMetadata.yearModel.year", yearModel.year)
                                    .equalTo("responseMetadata.studentCode", studCode).findFirst() ?: return null

        return dbResponse.toGradeResponseModel()
    }



    override fun createOrUpdate(response: GradeResponseModel, metadataModel: ResponseMetadataModel, realm: Realm) {
        val rlResponse = RlGradeResponseModel(metadataModel, response)
        realm.use {
            realm.beginTransaction()
            realm.insertOrUpdate(rlResponse)
            realm.commitTransaction()
            realm.close()
        }
    }
}