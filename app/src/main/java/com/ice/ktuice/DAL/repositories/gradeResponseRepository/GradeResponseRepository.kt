package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.scraper.models.GradeResponseModel
import com.ice.ktuice.scraper.models.ResponseMetadataModel
import com.ice.ktuice.scraper.models.YearModel
import io.realm.Realm


interface GradeResponseRepository {
    fun getByYearModel(studCode: String, yearModel: YearModel, realm: Realm): GradeResponseModel?
    fun createOrUpdate(response: GradeResponseModel, metadataModel: ResponseMetadataModel, realm: Realm)
}