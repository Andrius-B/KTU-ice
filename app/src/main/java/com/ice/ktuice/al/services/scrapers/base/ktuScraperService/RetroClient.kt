package com.ice.ktuice.al.services.scrapers.base.ktuScraperService

import retrofit2.Retrofit

object RetroClient {
    const val BASE_URL = "https://uais.cr.ktu.lt"
    val client = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()!!
}