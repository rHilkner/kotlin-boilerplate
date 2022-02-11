package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class StatusCd: PersistableEnum {

    ACTIVE {
        override var dbValue = "A"
    },
    INACTIVE {
        override var dbValue = "I"
    };

    class Converter : DbEnumConverter<StatusCd>(StatusCd::class.java)

}