package com.ice.ktuice.models.responses

import com.ice.ktuice.models.ModuleModel

/**
 * Created by Andrius on 1/23/2018.
 * Container for the module list and response code from the http request
 */
class ModuleResponseModel(val semester:Int, val statusCode:Int = 200): ArrayList<ModuleModel>()