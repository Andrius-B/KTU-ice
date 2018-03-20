package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import io.reactivex.subjects.Subject
import io.realm.RealmResults

/**
 * Created by Andrius on 2/24/2018.
 * Service for getting and saving the grades
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

    /**
     * Queries the database on the current device to get the local version of the calendar
     * (Used for viewing while updating and for comparing the new and old grade tables)
     */
    fun getYearGradesListFromDB(): YearGradesCollectionModel?

    /**
     * Scrapes the web to fetch the newest grade table
     */
    fun getYearGradesListFromWeb(): YearGradesCollectionModel?

    /**
     * Saves a the grades to the local device database
     */
    fun persistYearGradesModel(model: YearGradesCollectionModel)
}