package com.ice.ktuice.al.settings

class AppSettingsTestImpl: AppSettings {
    override var currentThemePos: Int
        get() = 0
        set(_) {}
    override var gradeNotificationsEnabled: Boolean
        get() = true
        set(_) {}
    override var networkingEnabled: Boolean
        get() = true
        set(_) {}
}