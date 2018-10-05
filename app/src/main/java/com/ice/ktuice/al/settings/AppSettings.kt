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
     * Weather notifications during a lecture will be shown
     */
    var persistentLectureNotificationEnabled: Boolean
}