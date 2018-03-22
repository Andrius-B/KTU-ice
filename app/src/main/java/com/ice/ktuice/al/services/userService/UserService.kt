package com.ice.ktuice.al.services.userService

import com.ice.ktuice.models.LoginModel

/**
 * Created by Andrius on 2/24/2018.
 */
interface UserService{
    /**
     * Gets the current login model if such exists on this device
     * Otherwise throws a null reference exception!
     */
    fun getLoginForCurrentUser(): LoginModel?

    /**
     * Refreshes the login cookies
     * (Because the KTU server has a timeout, where
     * after 10-15min of inactivity, the cookies are invalidated)
     */
    fun refreshLoginCookies(): LoginModel?
}