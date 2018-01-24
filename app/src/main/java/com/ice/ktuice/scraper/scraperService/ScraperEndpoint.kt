package com.ice.ktuice.scraper.scraperService

import com.ice.ktuice.scraper.scraperService.handlers.DataHandler
import com.ice.ktuice.scraper.handlers.LoginHandler
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.YearModel


object ScraperEndpoint {
    /**
     * Gets authentication cookies.
     *
     * @return Authentication cookies and user info
     */
    fun login(username: String, password: String)
            = LoginHandler().getAuthCookies(username, password)

    /**
     * Gets all marks for requested year.
     * TODO Group marks by the week.
     *
     * @param studCookie response from loginModel used for authentication.
     * @param yearModel from loginModel
     *
     *
     * @return List marks
     */
    fun getGrades(authCookie: LoginModel, yearModel: YearModel)
            = DataHandler().getGrades(authCookie, yearModel)

}

