package com.ice.ktuice.DAL.repositories.gradeResponseRepository

import com.ice.ktuice.models.YearGradesCollectionModel

/**
 * Created by Andrius on 2/19/2018.
 */
interface YearGradesRepository {
    fun getByStudCode(studCode: String): YearGradesCollectionModel?

    fun createOrUpdate(yearGradesModel: YearGradesCollectionModel)

    fun setUpdating(yearGradesModel: YearGradesCollectionModel, isUpdating: Boolean)
}