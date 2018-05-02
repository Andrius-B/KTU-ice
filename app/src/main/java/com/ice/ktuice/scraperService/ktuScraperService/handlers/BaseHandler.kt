package com.ice.ktuice.scraperService.ktuScraperService.handlers

import com.ice.ktuice.scraperService.ktuScraperService.RetroClient

open class BaseHandler {
    protected fun <T> Class<T>.create(): T
            = RetroClient.client.create(this)
}