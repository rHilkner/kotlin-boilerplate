package com.example.apiboilerplate.utils

object EnumUtil {

    fun <E : Enum<E>> stringToEnum(string: String, enumType: Class<E>) : E? {
        for (enum in enumType.enumConstants) {
            if (string == enum.name) {
                return enum
            }
        }
        return null
    }

    fun <E : Enum<E>> stringListToEnumList(strList: List<String>, enumType: Class<E>) : List<E> {
        val enumList: MutableList<E> = mutableListOf()
        for (string in strList) {
            stringToEnum(string, enumType)?.let { enumList.add(it) }
        }
        return enumList
    }

}