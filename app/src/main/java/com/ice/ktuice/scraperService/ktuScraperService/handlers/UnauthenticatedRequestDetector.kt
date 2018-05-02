package com.ice.ktuice.scraperService.ktuScraperService.handlers

import org.jsoup.nodes.Document

/**
 * Created by Andrius on 1/31/2018.
 */
class UnauthenticatedRequestDetector {
    companion object {
        private val unauthorizedRequestButtonValue = "Prisijungimas"
        private val unauthorizedRequestPostAction = "/ktuis/stp_prisijungimas"

        fun isResponseAuthError(doc: Document): Boolean{
            //info(doc.body())
            //val title = doc.select("title").firstOrNull()
            val button = doc.select("input").firstOrNull()
            val form = doc.select("form").firstOrNull()
            val lengthValid = doc.body().toString().length >= 50 // sometimes the response is just <body></body> which means no auth

            val buttonValMatch = button?.attr("value").equals(unauthorizedRequestButtonValue)
            val formActionMatch = form?.attr("action").equals(unauthorizedRequestPostAction)

            val isAuthenticated = buttonValMatch || formActionMatch || !lengthValid
            //info("Authenticated: $isAuthenticated")
            return isAuthenticated
        }
    }
}