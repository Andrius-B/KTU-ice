package com.ice.ktuice.repositories.yearGradesResponseRepository

import com.ice.ktuice.models.YearGradesCollectionModel

/**
 * Created by Andrius on 2/19/2018.
 * This repository is mainly for use in the YearGradesService, which should be the user-facing facade
 */
interface YearGradesRepository {
    fun getByStudCode(studCode: String): YearGradesCollectionModel?

    fun createOrUpdate(yearGradesModel: YearGradesCollectionModel)

    fun setUpdating(yearGradesModel: YearGradesCollectionModel, isUpdating: Boolean)
}