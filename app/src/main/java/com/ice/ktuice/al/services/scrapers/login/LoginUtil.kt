package com.ice.ktuice.al.services.scrapers.login

import java.net.URL
import java.net.URLDecoder

object LoginUtil {
    fun splitQuery(url: String): Map<String, String> {
        val queryPairs = LinkedHashMap<String, String>()
        val query = URL(url).query
        val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }
        return queryPairs
    }

    fun getAdditionalHeaders(): Map<String, String>
        = mapOf(Pair("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"))
}