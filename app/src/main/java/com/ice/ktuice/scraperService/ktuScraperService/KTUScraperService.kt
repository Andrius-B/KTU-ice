package com.ice.ktuice.scraperService.ktuScraperService

import com.ice.ktuice.scraperService.ktuScraperService.handlers.DataHandler
import com.ice.ktuice.scraperService.ktuScraperService.handlers.LoginHandler
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponse
import com.ice.ktuice.models.responses.YearGradesResponseModel
import com.ice.ktuice.scraperService.ScraperService
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.exceptions.ParsingException
import com.ice.ktuice.scraperService.exceptions.ServerErrorException
import com.ice.ktuice.scraperService.ktuScraperService.handlers.UnauthenticatedRequestDetector


class  KTUScraperService: ScraperService {

    override fun login(username: String, password: String)
            = LoginHandler().getAuthCookies(username, password)

    override fun getGrades(login: LoginModel, yearModel: YearModel): YearGradesModel {
        var response: YearGradesResponseModel
        try {
             response = DataHandler.getGrades(login, yearModel)
        }catch (e: AuthenticationException){
            throw e
        }catch (e:Exception){
            //Assume that parsing failed in this case
            //(pretty much if anything else goes wrong)
            response = YearGradesResponseModel(-100, YearGradesModel(yearModel))
        }

        println("Grades response code(@Scraper service):"+response.statusCode)
        if(response.statusCode >= 500){
            throw ServerErrorException("Server error, something went wrong!")
        }else if(response.statusCode <= -100){
            throw ParsingException("Parsing failed!(or something else..)")
        }
        return response.yearGradesModel
    }

    override fun getAllGrades(loginModel: LoginModel): YearGradesCollectionModel {
        val grades = YearGradesCollectionModel(loginModel.studentId)
        loginModel.studentSemesters.forEach {
            try {
                val yearModel = getGrades(loginModel, it)
                grades.add(yearModel)
            }catch (parsingException: ParsingException){
            }
        }
        return grades
    }

    override fun refreshLoginCookies(login: LoginModel): LoginResponse {
        return login(login.username, login.password)
    }

}

