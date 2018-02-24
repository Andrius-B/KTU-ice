package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.models.YearGradesModel
import io.realm.RealmResults

/**
 * Created by Andrius on 2/24/2018.
 */
interface YearGradesService {
    fun getYearGradesListFromWeb(): List<YearGradesModel>

    fun getYearGradesListFromDB(): RealmResults<YearGradesModel>

    fun persistYearGradeModels(modelList: List<YearGradesModel>)

    fun persistYearGradeModel(model: YearGradesModel)
}