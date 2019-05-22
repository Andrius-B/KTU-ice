package com.ice.ktuice.al.services.scrapers.login.stages

import com.ice.ktuice.al.services.scrapers.login.LoginDataStore

abstract class Stage {
    /**
     * An abstract stage in the login flow
     * @param cookieJar - the current content of the cookie jar
     * @param dataStore - a storage to keep the state of login flow
     *
     * @return status code of the stage (much like a http response code)
     */
    abstract fun execute(cookieJar: HashMap<String, String>, dataStore: LoginDataStore): Int
}