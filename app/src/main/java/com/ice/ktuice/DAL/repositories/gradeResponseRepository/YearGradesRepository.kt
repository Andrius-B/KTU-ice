package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import io.realm.RealmResults

/**
 * Created by Andrius on 2/19/2018.
 */
interface YearGradesRepository {
    fun getByStudCode(studCode: String): YearGradesCollectionModel?

    fun createOrUpdate(yearGradesModel: YearGradesCollectionModel)

    fun setUpdating(yearGradesModel: YearGradesCollectionModel, isUpdating: Boolean)
}