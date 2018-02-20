package com.ice.ktuice.AL.koinModules

import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.AL.GradeTable.yearGradesModelComparator.YearGradesModelComparatorTestImpl
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepositoryImpl
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.SharedPreferenceRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * Created by Andrius on 1/31/2018.
 */
val mainModule: Module = applicationContext {
    provide { SharedPreferenceRepositoryImpl(this.androidApplication()) as PreferenceRepository }
    provide { YearGradesModelComparatorTestImpl() as YearGradesModelComparator }
}