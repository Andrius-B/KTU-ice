package com.ice.ktuice.scraper.scraperService

import com.ice.ktuice.scraper.scraperService.handlers.DataHandler
import com.ice.ktuice.scraper.handlers.LoginHandler
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.YearGradesModel
import com.ice.ktuice.scraper.models.YearModel
import com.ice.ktuice.scraper.scraperService.Exceptions.AuthenticationException
import com.ice.ktuice.scraper.scraperService.Exceptions.ServerErrorException


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
        val response = DataHandler.getGrades(authCookie, yearModel)
        println("Grades response code(@Scraper service):"+response.statusCode)
        if(response.statusCode == 401){
            throw AuthenticationException("Getting grades failed, because the cookies on the login model are incorrect!")
        }else if(response.statusCode >= 500){
            throw ServerErrorException("Server error, something went wrong!")
        }
        return response.yearGradesModel
    }

}

