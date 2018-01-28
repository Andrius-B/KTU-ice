package com.ice.ktuice.scraper.models

class MarkModel(
        val name: String,
        val id: String,
        val semester: String,
        val module_code: String,
        val module_name: String,
        val semester_number: String,
        val credits: String,
        val language: String,
        val professor: String,
        val typeId: String,
        val type: String?,
        val week: String,
        val marks: MutableList<String>
){
    fun getMarkDisplayString():String{
        var text = ""
        marks.forEachIndexed{index, mark ->
            text += mark
            if(index != marks.size) text += " "
        }
        return text
    }
}