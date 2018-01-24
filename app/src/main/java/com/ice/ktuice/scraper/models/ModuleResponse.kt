package com.ice.ktuice.scraper.models

/**
 * Created by Andrius on 1/23/2018.
 * Container for the module list and response code from the http request
 */
class ModuleResponse(val statusCode:Int = 200): ArrayList<ModuleModel>()