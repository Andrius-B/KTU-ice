package com.ice.ktuice.scraperTests

import com.ice.ktuice.al.services.scrapers.login.LoginServiceImpl
import com.ice.ktuice.impl.FileLoginProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginServiceTest {
    lateinit var username: String
    lateinit var password: String

    @Before
    fun init(){
        val l = FileLoginProvider.getLoginFromFile()
        username = l.first
        password = l.second
    }

    @Test
    fun `Test login sequence`(){
        val loginService = LoginServiceImpl()
        val loginResponseModel = loginService.login(username, password)
        println("LoginResponseModel:$loginResponseModel")
        assertTrue(loginResponseModel.getCookieMap().contains("STUDCOOKIE"))
        assertTrue(loginResponseModel.getCookieMap()["STUDCOOKIE"]?.isNotBlank()!!)
    }
}