package com.ice.ktuice.scraper.handlers

import com.ice.ktuice.scraper.scraperService.RetroClient

open class BaseHandler {
    protected fun <T> Class<T>.create(): T
            = RetroClient.client.create(this)
}