package com.ice.ktuice.scraper.models

/**
 * Created by Andrius on 2/11/2018.
 */
class YearGradesModel(
        val year: YearModel,
        val semesterList: MutableList<SemesterModel> = mutableListOf()
)