package com.ice.ktuice.scraperService

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponse
import com.ice.ktuice.scraperService.ktuScraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.ktuScraperService.exceptions.ServerErrorException

interface ScraperService {
    /**
     * Gets authentication cookies
     * and general information about a student
     * @return Authentication cookies and user info
     */
    fun login(username: String, password: String): LoginResponse

    /**
     * Gets all marks for requested year.
     *
     * @param login response from loginModel used for authentication.
     * @param yearModel from loginModel
     * @return List marks
     *
     * @throws AuthenticationException if cookies timeout or are incorrect
     * @throws ServerErrorException if the server responds with code 500
     */
    fun getGrades(login: LoginModel, yearModel: YearModel): YearGradesModel

    /**
     * Used for refreshing cookies (if say fetching the grades fails, the service assumes that
     * the cookies are expired and tries to refresh the cookies at least once)
     * works exactly as the login() function, just for convenience
     */
    fun refreshLoginCookies(login: LoginModel): LoginResponse
}