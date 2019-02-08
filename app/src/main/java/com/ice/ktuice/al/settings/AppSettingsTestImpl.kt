package com.ice.ktuice.al.settings

class AppSettingsTestImpl: AppSettings {
    override var currentThemePos: Int
        get() = 0
        set(value) {}
    override var gradeNotificationsEnabled: Boolean
        get() = true
        set(value) {}
    override var networkingEnabled: Boolean
        get() = true
        set(value) {}
}