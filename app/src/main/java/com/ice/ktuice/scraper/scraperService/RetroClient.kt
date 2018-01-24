package com.ice.ktuice.scraper.scraperService

import retrofit2.Retrofit

object RetroClient {
    const val BASE_URL = "https://uais.cr.ktu.lt"
    val client = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()!!
}