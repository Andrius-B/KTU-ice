package com.ice.ktuice.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class YearModel(
        var id :String = "",
        var year: String = ""
): RealmObject()