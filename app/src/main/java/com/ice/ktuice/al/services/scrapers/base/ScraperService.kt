package com.ice.ktuice.al.services.scrapers.base

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponseModel
import com.ice.ktuice.al.services.scrapers.base.exceptions.AuthenticationException
import com.ice.ktuice.al.services.scrapers.base.exceptions.ServerErrorException

interface ScraperService {
    /**
     * Gets authentication cookies
     * and general information about a student
     * @return Authentication cookies and user info
     */
    fun login(username: String, password: String): LoginResponseModel

    /**
     * Gets all marks for requested year.
     *
     * @param login response from loginModel used for authentication.
     * @param yearModel from loginModel, the semesters to be fetched
     * @return List marks (might be empty)
     *
     * @throws AuthenticationException if cookies timeout or are incorrect
     * @throws ParsingException if there is any other problem
     * @throws ServerErrorException if the server responds with code 500
     */
    fun getGrades(login: LoginModel, yearModel: YearModel): YearGradesModel

    /**
     * Foreaches over all semesters in the login model and returns a collection of all
     * valid semester grade models
     *
     * @throws AuthenticationException if cookies timeout or are incorrect
     * @throws ParsingException if there is any other problem
     * @throws ServerErrorException if the server responds with code 500
     */
    fun getAllGrades(loginModel: LoginModel): YearGradesCollectionModel

    /**
     * Used for refreshing cookies (if say fetching the grades fails, the service assumes that
     * the cookies are expired and tries to refresh the cookies at least once)
     * works exactly as the login() function, just for convenience
     */
    fun refreshLoginCookies(login: LoginModel): LoginResponseModel
}