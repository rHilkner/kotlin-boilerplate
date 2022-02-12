package com.example.apiboilerplate.base.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

lateinit var log: Logger
class ApiLogger : ReadOnlyProperty<Any, Logger> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        if (!::log.isInitialized) log = LoggerFactory.getLogger(thisRef.javaClass)
        return log
    }

}