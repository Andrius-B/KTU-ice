package com.ice.ktuice.viewModels.gradesFragment

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.ui.login.LoginActivity
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * View model contains the data needed to show the
 * FragmentGrades and some additional fields to improve
 * user experience
 */
class GradesFragmentViewModel: ViewModel(), KoinComponent, Disposable{
    private val userService: UserService by inject()
    private val yearGradesService: YearGradesService by inject()
    private val preferenceRepository:PreferenceRepository by inject()

    /**
     * Private mutable versions with the m* prefix and public
     * implicit cast to immutable live data.
     */
    private val mLoginModel = MutableLiveData<LoginModel>()
    val loginModel: LiveData<LoginModel>
        get() = mLoginModel

    private val gradesData = MutableLiveData<YearGradesCollectionModel>()
    val grades: LiveData<YearGradesCollectionModel>
        get() = gradesData

    val selectedYear = MutableLiveData<String>()
    val selectedSemesterNumber = MutableLiveData<String>()

    val yearGradesSubjectDisposable: Disposable

    init {
        val selectedSemesterNumberVal = preferenceRepository.getValue(R.string.currently_selected_semester_id)
        selectedSemesterNumber.postValue(selectedSemesterNumberVal)

        val selectedYearStrVal = preferenceRepository.getValue(R.string.currently_selected_year_id)
        selectedYear.postValue(selectedYearStrVal)

        val loginModelValue = userService.getLoginForCurrentUser()!!
        mLoginModel.postValue(loginModelValue)

        val yearGrades = yearGradesService.getYearGradesListSubject()
        yearGradesSubjectDisposable = yearGrades.subscribe{
            gradesData.postValue(it)
        }
    }

    fun logoutCurrentUser(activity: Activity?){
        activity?.runOnUiThread{
            preferenceRepository.setValue(R.string.logged_in_user_code, "") // clear out the logged in user code from prefrences
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(activity, intent, null)
            activity.finish()
        }
    }

    override fun isDisposed(): Boolean {
        return  yearGradesSubjectDisposable.isDisposed
    }

    override fun dispose() {
        return yearGradesSubjectDisposable.dispose()
    }

}