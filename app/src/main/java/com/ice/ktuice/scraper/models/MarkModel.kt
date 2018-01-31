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
        /**
         * A list of marks: higher index strings are newer overridden marks.
         */
        val marks: MutableList<String>
){
        fun isEmpty():Boolean{
                var empty = true
                marks.forEach {
                        if(!it.isBlank()) empty = false // and if atleast one mark is not blank
                        // the mark is not empty!
                }
                return empty
        }
}