package com.ice.ktuice.scraper.scraperService.handlers

import org.jsoup.nodes.Document

/**
 * Created by Andrius on 1/31/2018.
 */
class UnauthenticatedRequestDetector {
    companion object {
        private val unauthorizedRequestTitle = "Neautentifikuotas priėjimas prie informacinės sistemos"
        private val unauthorizedRequestButtonValue = "Prisijungimas"
        private val unauthorizedRequestPostAction = "/ktuis/stp_prisijungimas"

        fun isResponseAuthError(doc: Document): Boolean{
            val assurance = 0

            val title = doc.select("title").firstOrNull()
            val button = doc.select("input").firstOrNull()
            val form = doc.select("form").firstOrNull()

            val titleMatch = title?.text().equals(unauthorizedRequestTitle)
            val buttonValMatch = button?.attr("value").equals(unauthorizedRequestButtonValue)
            val formActionMatch = form?.attr("action").equals(unauthorizedRequestPostAction)

            return titleMatch && buttonValMatch && formActionMatch
        }
    }
}