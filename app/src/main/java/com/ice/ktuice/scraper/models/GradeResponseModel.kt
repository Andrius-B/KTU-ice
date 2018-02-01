package com.ice.ktuice.scraper.models

/**
 * Created by Andrius on 1/23/2018.
 * A helper class to contain both the list of marks from the scraper service
 * and the response code from the http get.
 */
class GradeResponseModel(val statusCode: Int) : ArrayList<GradeModel>()