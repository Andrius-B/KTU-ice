package com.ice.ktuice.al.services.scraperService.ktuScraperService.handlers

import com.ice.ktuice.al.services.scraperService.ktuScraperService.RetroClient

open class BaseHandler {
    protected fun <T> Class<T>.create(): T
            = RetroClient.client.create(this)
}