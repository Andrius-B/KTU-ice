package com.ice.ktuice.scraper.models


data class LoginModel(
        val authCookies: Map<String, String>,
        val studentName: String = "",
        val studentId: String = "",
        val studentSemesters: List<YearModel>
)