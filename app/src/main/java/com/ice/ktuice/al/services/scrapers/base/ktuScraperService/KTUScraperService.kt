package com.ice.ktuice.al.services.scrapers.base.ktuScraperService

import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.handlers.DataHandler
import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.handlers.LoginHandler
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.models.responses.LoginResponseModel
import com.ice.ktuice.models.responses.YearGradesResponseModel
import com.ice.ktuice.al.services.scrapers.base.ScraperService
import com.ice.ktuice.al.services.scrapers.base.exceptions.AuthenticationException
import com.ice.ktuice.al.services.scrapers.base.exceptions.ParsingException
import com.ice.ktuice.al.services.scrapers.base.exceptions.ServerErrorException
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class  KTUScraperService: ScraperService, AnkoLogger{

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

        info("Grades response code(@Scraper service):"+response.statusCode)
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

    override fun refreshLoginCookies(login: LoginModel): LoginResponseModel {
        return login(login.username, login.password)
    }

}

