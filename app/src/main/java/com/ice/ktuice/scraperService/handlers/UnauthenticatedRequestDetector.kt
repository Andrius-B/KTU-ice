package com.ice.ktuice.scraperService.handlers

import org.jsoup.nodes.Document

/**
 * Created by Andrius on 1/31/2018.
 */
class UnauthenticatedRequestDetector {
    companion object {
        private val unauthorizedRequestButtonValue = "Prisijungimas"
        private val unauthorizedRequestPostAction = "/ktuis/stp_prisijungimas"

        fun isResponseAuthError(doc: Document): Boolean{
            //val title = doc.select("title").firstOrNull()
            val button = doc.select("input").firstOrNull()
            val form = doc.select("form").firstOrNull()

            val buttonValMatch = button?.attr("value").equals(unauthorizedRequestButtonValue)
            val formActionMatch = form?.attr("action").equals(unauthorizedRequestPostAction)

            return buttonValMatch && formActionMatch
        }
    }
}