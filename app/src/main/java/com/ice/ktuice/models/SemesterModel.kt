package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by Andrius on 2/11/2018.
 */
@RealmClass
open class SemesterModel(var semester:String = "",
                         var semester_number:String = "",
                         var moduleList: RealmList<ModuleModel> = RealmList()
): RealmObject()