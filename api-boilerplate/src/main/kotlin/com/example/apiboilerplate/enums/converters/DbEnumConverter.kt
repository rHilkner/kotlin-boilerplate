package com.example.apiboilerplate.enums.converters

import javax.persistence.AttributeConverter
import javax.persistence.Converter


/**
 * Created by rodrigohilkner on Feb, 2020
 */
@Converter
abstract class DbEnumConverter<T>(private val enumClass: Class<T>) :
    AttributeConverter<T?, String?> where T : Enum<T>, T : PersistableEnum {

    override fun convertToDatabaseColumn(attribute: T?): String? {
        return attribute?.dbValue
    }

    override fun convertToEntityAttribute(dbData: String?): T? {
        val enums = enumClass.enumConstants
        for (e in enums) {
            if (e.dbValue == dbData) {
                return e
            }
        }
        throw UnsupportedOperationException()
    }

}