package com.ice.ktuice.models.responses
import com.ice.ktuice.models.TimetableModel

class TimetableResponseModel(
        val timetableModel: TimetableModel,
        val statusCode: Int = 200
)