package com.ice.ktuice.al.services.userService

import com.ice.ktuice.models.LoginModel

/**
 * Created by Andrius on 2/24/2018.
 */
interface UserService{
    fun getLoginForCurrentUser(): LoginModel?
    fun refreshLoginCookies(): LoginModel?
}