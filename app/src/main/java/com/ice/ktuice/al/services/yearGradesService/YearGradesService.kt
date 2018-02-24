package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import io.realm.RealmResults

/**
 * Created by Andrius on 2/24/2018.
 */
interface YearGradesService {
    fun getYearGradesListFromWeb(): YearGradesCollectionModel

    fun getYearGradesListFromDB(async: Boolean = false): YearGradesCollectionModel

    fun persistYearGradesModel(model: YearGradesCollectionModel)
}