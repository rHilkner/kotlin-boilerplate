package com.example.apiboilerplate.utils

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties

object ObjectUtil {

    fun <T : Any, R : Any> copyProps(fromObject: R, toObject: T, vararg props: KProperty<*>): T {

        // only consider mutable properties
        val mutableProps = toObject::class.memberProperties.filterIsInstance<KMutableProperty<*>>()

        // if source list is provided use that otherwise use all available properties
        val sourceProps = if (props.isEmpty()) fromObject::class.memberProperties else props.toList()

        // copy all matching
        mutableProps.forEach { targetProp ->
            sourceProps.find {
                // make sure properties have same name and compatible types
                it.name == targetProp.name && targetProp.returnType.isSupertypeOf(it.returnType)
            }?.let { matchingProp ->
                targetProp.setter.call(toObject, matchingProp.getter.call(fromObject))
            }
        }

        return toObject
    }

}