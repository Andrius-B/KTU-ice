package com.ice.ktuice.UI.main.components.gradeTable

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ice.ktuice.AL.GradeTable.GradeTableManager
import com.ice.ktuice.AL.GradeTable.GradeTableModels.GradeTableModel
import com.ice.ktuice.AL.GradeTable.GradeTableModels.SemesterAdapterItem
import com.ice.ktuice.R
import com.ice.ktuice.UI.main.GradeTableCellDetailsDialog
import com.ice.ktuice.models.YearGradesModel
import kotlinx.android.synthetic.main.grade_table_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread

/**
 * Created by Andrius on 1/29/2018.
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs){
    private val tableCellDetailsDialog: GradeTableCellDetailsDialog = GradeTableCellDetailsDialog(context)
    private var tableModel: GradeTableModel? = null

    private val CELL_PADDING_H: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_horizontal)
    private val CELL_PADDING_V: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_vertical)
    private val CELL_TEXT_SIZE = context.resources.getInteger(R.integer.grade_table_cell_text_size).toFloat()



    constructor(c: Context): this(c, null)

    init {
        inflate(context, R.layout.grade_table_layout, this)
        val tableManager = GradeTableManager()
        doAsync({
            println(it.getStackTraceString())
        },{

            uiThread {
                val login = tableManager.getLoginForCurrentUser()
                println("User has semesters:"+login.studentSemesters.size)
                val grades = tableManager.getYearGradesListFromDB(login).toList()
                //val grades = tableManager.getYearGradesListFromWeb(login)
                val semesterSpinnerItems = tableManager.constructSemesterAdapterSpinnerItemList(grades)
                tableModel = tableManager.constructGradeTableModel(login, grades)
                createViewForModel(tableModel!!)
                println("Persisting grade table from UI thread!")
                setUpSemesterSpinner(semesterSpinnerItems)
                //tableManager.persistYearGradeModels(grades)
            }
        })
    }

    private fun createViewForModel(gradeTableModel: GradeTableModel, semesterAdapterItem: SemesterAdapterItem? = null){
        grade_table_table_layout.removeAllViews()
        grade_table_headers.removeAllViews()

        if(semesterAdapterItem != null){
            gradeTableModel.selectSemester(semesterAdapterItem.semesterNumber)
        }

        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val tableRow = TableRow(context)
        weekModelList?.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            tableRow.addView(weekText)
        }
        grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        // add a spacer dummy text to keep the spacing even for the table and module names
        grade_table_headers.addView(TextView(context), ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        rowModelList?.forEach {
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            moduleNameText.setSingleLine(true)
            moduleNameText.ellipsize = TextUtils.TruncateAt.END
            moduleNameText.maxLines = 1
            moduleNameText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
            moduleNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
            moduleNameText.setBackgroundResource(R.drawable.grade_cell_background)

            grade_table_headers.addView(moduleNameText)

            weekModelList?.forEach { weekModel ->
                val markCellText = TextView(context)
                markCellText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
                markCellText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
                val cellModel = it.getByWeekModel(weekModel)
                markCellText.text = cellModel?.getDisplayString() // default is empty cell
                markCellText.setOnClickListener{
                    markCellText.post({
                        tableCellDetailsDialog.CellModel = cellModel
                        tableCellDetailsDialog.show()
                    })
                }
                markCellText.setBackgroundResource(R.drawable.grade_cell_background)
                tableRow.addView(markCellText)
            }
            grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun setUpSemesterSpinner(itemList: List<SemesterAdapterItem>){
        val adapter = SemesterSpinnerAdapter(context, itemList)
        grade_table_semmester_spinner.adapter = adapter
        grade_table_semmester_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = adapter.getItem(p2)
                createViewForModel(tableModel!!, item)
            }

        }
    }


}