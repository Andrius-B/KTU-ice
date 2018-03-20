package com.ice.ktuice.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class YearModel(
        var id :String = "",
        var year: String = ""
): RealmObject(){
    override fun equals(other: Any?): Boolean {
        val otherYear  = other as YearModel
        return this.id.equals(otherYear.id) && this.year.equals(otherYear.year)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + year.hashCode()
        return result
    }
}