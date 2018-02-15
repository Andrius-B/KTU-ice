package com.ice.ktuice.UI.main.components.gradeTable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.SemesterModel
import com.ice.ktuice.scraper.models.YearGradesModel
import com.ice.ktuice.scraper.models.YearModel

/**
 * Created by Andrius on 2/15/2018.
 */
class SemesterSpinnerAdapter(val context: Context, yearsList: List<YearGradesModel>): BaseAdapter() {
    private val itemList: MutableList<SemesterAdapterItem> = mutableListOf()

    init{
        yearsList.forEach {
            val year = it.year
            it.semesterList.forEach {
                itemList.add(SemesterAdapterItem(it.semester, it.semester_number, year))
            }
        }
    }

    override fun getView(index: Int, recycleView: View?, parent: ViewGroup?): View {
        val model = itemList[index]
        val view = if(recycleView == null){
            LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        }else{
            recycleView
        }

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = String.format("%s \\ %s", model.year.id, model.semester)

        return view
    }

    override fun getItem(index: Int) = itemList[index]

    override fun getItemId(index: Int) = index.toLong()

    override fun getCount() = itemList.size


    class SemesterAdapterItem(val semester: String, val semesterNumber: String,val year: YearModel)
}