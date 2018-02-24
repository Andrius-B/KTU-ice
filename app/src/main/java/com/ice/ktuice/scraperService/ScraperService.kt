package com.ice.ktuice.scraperService

import com.ice.ktuice.scraperService.handlers.DataHandler
import com.ice.ktuice.scraper.handlers.LoginHandler
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.YearGradesResponseModel
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.exceptions.ServerErrorException


object ScraperService {
    /**
     * Gets authentication cookies
     * and general information about a student
     * @return Authentication cookies and user info
     */
    fun login(username: String, password: String)
            = LoginHandler().getAuthCookies(username, password)

    /**
     * Gets all marks for requested year.
     *
     * @param authCookie response from loginModel used for authentication.
     * @param yearModel from loginModel
     * @return List marks
     *
     * @throws AuthenticationException if cookies timeout or are incorrect
     * @throws ServerErrorException if the server responds with code 500
     */
    fun getGrades(authCookie: LoginModel, yearModel: YearModel): YearGradesModel {
        var response: YearGradesResponseModel
        try {
             response = DataHandler.getGrades(authCookie, yearModel)
        }catch (e: AuthenticationException){
            println("Cookies expired! Logging in again!")
            val newLogin = refreshLoginCookies(authCookie)!!
            response = DataHandler.getGrades(newLogin, yearModel)
        }

        println("Grades response code(@Scraper service):"+response.statusCode)
        if(response.statusCode >= 500){
            throw ServerErrorException("Server error, something went wrong!")
        }
        return response.yearGradesModel
    }

    fun refreshLoginCookies(login: LoginModel): LoginModel?{
        return login(login.username, login.password).loginModel
    }

}

