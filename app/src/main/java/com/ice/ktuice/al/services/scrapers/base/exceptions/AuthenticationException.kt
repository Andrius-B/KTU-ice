package com.ice.ktuice.al.services.scrapers.base.exceptions

/**
 * Created by Andrius on 1/31/2018.
 * Exception raised by the auth service when the username or password are not correct
 */
class AuthenticationException(message: String = ""): Exception(message)