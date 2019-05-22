package com.ice.ktuice.al.services.scrapers.login

/**
 * Simple wrapper around a hashmap to make data more explicit
 */
class LoginDataStore: HashMap<String, String>() {
    var userName: String
        get() = this["username"] ?: ""
        set(value){
            this["username"] = value
        }

    var password: String
        get() = this["password"] ?: ""
        set(value){
            this["password"] = value
        }

    var authState: String
        get() = this["AuthState"] ?: ""
        set(value){
            setWithBackup("AuthState", value)
        }

    var stateId: String
        get() = this["StateId"] ?: ""
        set(value){
            setWithBackup("StateId", value)
        }

    var samlResponse: String
        get() = this["SAMLResponse"] ?: ""
        set(value){
            setWithBackup("SAMLResponse", value)
        }

    var relayState: String
        get() = this["RelayState"] ?: ""
        set(value){
            setWithBackup("RelayState", value)
        }

    /**
     * In case a variable is being overridden, back it up
     */
    fun setWithBackup(key: String, value: String){
        if(this.containsKey(key)) {
            var oldKey = key
            var i = 1
            while (this.containsKey(oldKey)) {
                oldKey = "$key$i"
                i++
            }
            // back up the old data
            this[oldKey] = this[key]!!
        }
        this[key] = value
    }
}