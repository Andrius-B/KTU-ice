package com.ice.ktuice.scraperService.ktuScraperService

import com.ice.ktuice.scraperService.ktuScraperService.handlers.DataHandler
import com.ice.ktuice.scraperService.ktuScraperService.handlers.LoginHandler
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponse
import com.ice.ktuice.models.responses.YearGradesResponseModel
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.ktuScraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.ktuScraperService.exceptions.ServerErrorException


class  KTUScraperService: ScraperService {

    override fun login(username: String, password: String)
            = LoginHandler().getAuthCookies(username, password)

    override fun getGrades(login: LoginModel, yearModel: YearModel): YearGradesModel {
        var response: YearGradesResponseModel
        try {
             response = DataHandler.getGrades(login, yearModel)
        }catch (e: AuthenticationException){
            println("Cookies expired! Logging in again!")
            val newLogin = refreshLoginCookies(login).loginModel!!
            response = DataHandler.getGrades(newLogin, yearModel)
        }

        println("Grades response code(@Scraper service):"+response.statusCode)
        if(response.statusCode >= 500){
            throw ServerErrorException("Server error, something went wrong!")
        }
        return response.yearGradesModel
    }

    override fun refreshLoginCookies(login: LoginModel): LoginResponse {
        return login(login.username, login.password)
    }

}

