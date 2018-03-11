package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import io.reactivex.subjects.Subject
import io.realm.RealmResults

/**
 * Created by Andrius on 2/24/2018.
 */
interface YearGradesService {

    /**
     * Queries the database and fetches the database version first, then fetches
     * the newest version from the web
     */
    fun getYearGradesList(): Subject<YearGradesCollectionModel>

    /**
     * This function returns a cached subject of the past observed versions!
     */
    fun getYearGradesListSubject(): Subject<YearGradesCollectionModel>?

    fun getYearGradesListFromDB(): YearGradesCollectionModel?

    fun getYearGradesListFromWeb(): YearGradesCollectionModel?

    fun persistYearGradesModel(model: YearGradesCollectionModel)
}