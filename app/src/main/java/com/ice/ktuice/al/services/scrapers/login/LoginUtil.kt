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
}