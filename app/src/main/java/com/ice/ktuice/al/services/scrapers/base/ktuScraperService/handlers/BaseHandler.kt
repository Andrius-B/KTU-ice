package com.ice.ktuice.al.services.scrapers.base.ktuScraperService.handlers

import com.ice.ktuice.al.services.scrapers.base.ktuScraperService.RetroClient

open class BaseHandler {
    protected fun <T> Class<T>.create(): T
            = RetroClient.client.create(this)
}