package com.ice.ktuice.al.settings

interface AppSettings {
    /**
     *  Describes the currently selected theme:
     *  0 - light
     *  1 - dark
     */
    var currentThemePos: Int

    /**
     * Weather notifications on gradeTable updates will be pushed
     */
    var gradeNotificationsEnabled: Boolean

    /**
     * Weather the app should query the target website for new information
     * ** This is supposed to allow concurrent usage of both the app and the web version of the
     *      AIS - since it only gives out a single cookie, we must disable all networking here
     *      to be able to use the web interface without getting logged out.
     */
    var networkingEnabled: Boolean
}