package com.ice.ktuice.ui.main.components.gradeTable

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ice.ktuice.al.GradeTable.GradeTableManager
import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableModel
import com.ice.ktuice.al.GradeTable.gradeTableModels.SemesterAdapterItem
import com.ice.ktuice.R
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.ui.adapters.SemesterSpinnerAdapter
import com.ice.ktuice.ui.main.dialogs.GradeTableCellDetailsDialog
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.grade_table_layout.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 1/29/2018.
 * The Grade table component contains a spinner for semester selection,
 * and then the grades in a table, that correspond to the selected semester.
 * TODO move all the application logic to GradeTableManager
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs), KoinComponent {
    private val userService: UserService by inject()
    private val yearGradesService: YearGradesService by inject()

    private var tableCellDetailsDialog: GradeTableCellDetailsDialog? = null
    private var tableModel: GradeTableModel? = null

    private val CELL_PADDING_H: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_horizontal)
    private val CELL_PADDING_V: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_vertical)
    private val CELL_TEXT_SIZE = context.resources.getInteger(R.integer.grade_table_cell_text_size).toFloat()


    private val tableManager = GradeTableManager()
    constructor(c: Context): this(c, null)


    init {
        inflate(context, R.layout.grade_table_layout, this)

        val login = userService.getLoginForCurrentUser()!!
        println("User has semesters:"+login.studentSemesters.size)
        val gradesSubject  = yearGradesService.getYearGradesList()
        gradesSubject.subscribe{
            println("Grades change detected!")
            println("New Grades valid:"+it.isValid)
            if(it.isEmpty()){
                /*
                    On the initial load, the returned value is just a placeholder,
                    here we subscribe to the whole realm to keep track of the freshly loaded
                    grade table
                 */
                isLoadingOverlayShown = true
            }else{
                updateGradeTable(it)
            }
        }
    }


    private fun updateGradeTable(grades: YearGradesCollectionModel){
        if(grades.yearList.size == 0){

            return@updateGradeTable
        }
        isLoadingOverlayShown = false
        if(grade_table_semmester_spinner.adapter == null){
            //construct table spinner on initial widget construction
            val semesterSpinnerItems = tableManager.constructSemesterAdapterSpinnerItemList(grades)
            setUpSemesterSpinner(semesterSpinnerItems)
            grade_table_semmester_spinner.setSelection(semesterSpinnerItems.lastIndex, true)
        }
        val changedSemesterSpinnerItems = tableManager.constructSemesterAdapterSpinnerItemList(grades)
        val changedTableModel = tableManager.constructGradeTableModel(grades)
        val selectedSemesterSpinnerItem = grade_table_semmester_spinner.adapter.getItem(grade_table_semmester_spinner.selectedItemPosition) as SemesterAdapterItem

        setUpSemesterSpinner(changedSemesterSpinnerItems)
        tableModel = changedTableModel
        grade_table_semmester_spinner.setSelection(changedSemesterSpinnerItems.indexOfFirst { it.semesterNumber.equals(selectedSemesterSpinnerItem.semesterNumber) })
        println("_____________________________\n\r--------TABLE UPDATED--------")
    }

    /**
     * Main view inflation and recycling in this function:
     * it creates a table view for the specified model and if provided, the selected semester
     */
    private fun createViewForModel(gradeTableModel: GradeTableModel, semesterAdapterItem: SemesterAdapterItem? = null){
        grade_table_table_layout.removeAllViews()
        grade_table_headers.removeAllViews()

        if(semesterAdapterItem != null)gradeTableModel.selectSemester(semesterAdapterItem.semesterNumber)
        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val weekTableRow = TableRow(context)
        weekModelList?.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            weekText.setBackgroundResource(R.drawable.grade_cell_background)
            weekTableRow.addView(weekText)
        }
        grade_table_table_layout.addView(weekTableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        val weekTitleContainer = LinearLayout(context)
        val weekTitle = TextView(context)
        weekTitle.setText(R.string.grade_table_week_header)
        weekTitleContainer.setPadding(CELL_PADDING_H,0,0,0)
        weekTitle.setBackgroundResource(R.drawable.grade_cell_background)
        weekTitleContainer.addView(weekTitle)
        weekTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        grade_table_headers.addView(weekTitleContainer, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        rowModelList?.forEach {
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            moduleNameText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
            moduleNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)

            grade_table_headers.addView(moduleNameText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

            weekModelList?.forEach { weekModel ->
                val markCellText = TextView(context)
                markCellText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
                markCellText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
                val cellModel = it.getByWeekModel(weekModel)
                markCellText.text = cellModel?.getDisplayString() // default is empty cell
                markCellText.setOnClickListener{
                    markCellText.post({
                        tableCellDetailsDialog?.CellModel = cellModel
                        tableCellDetailsDialog?.show()
                    })
                }
                tableRow.addView(markCellText)
            }
            grade_table_table_layout.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        }

        val rPadding = grade_table_grade_scroll_view_content.paddingRight
        val bPadding = grade_table_grade_scroll_view_content.paddingBottom
        val tPadding = grade_table_grade_scroll_view_content.paddingTop
        val headerWidth = grade_table_headers.measuredWidth

        grade_table_grade_scroll_view_content.setPadding(headerWidth, tPadding, rPadding, bPadding)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grade_table_grade_scroll_view.setOnScrollChangeListener{
                view, scrollX, scrollY, oldScrollX, oldScrollY ->
                //TODO make a toolbar-esque animation for the table headers
                grade_table_headers.x = -scrollX.toFloat()
            }
        }else{
            //TODO hide headers on Build < M
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

    /**
     * Displays or hides the simple loading overlay, that covers the whole view
     */
    private var isLoadingOverlayShown: Boolean
        get() = loading_overlay.isShown
        set(value){
            val newVisibility = if(value)View.VISIBLE
                                    else View.GONE
            loading_overlay.visibility = newVisibility
        }
}