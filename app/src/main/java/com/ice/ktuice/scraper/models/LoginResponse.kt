package com.ice.ktuice.scraper.models

/**
 * Created by Andrius on 1/23/2018.
 * Contains both the login model and response code from the http request
 */
class LoginResponse(val loginModel: LoginModel?, val statusCode:Int)