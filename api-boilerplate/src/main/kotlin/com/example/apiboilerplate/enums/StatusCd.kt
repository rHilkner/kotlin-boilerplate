package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DTOEnum
import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class StatusCd: PersistableEnum, DTOEnum {

    ACTIVE {
        override var dbValue = "A"
        override var dtoValue = "ACTIVE"
    },
    INACTIVE {
        override var dbValue = "I"
        override var dtoValue = "INACTIVE"
    };

    class Converter : DbEnumConverter<StatusCd>(StatusCd::class.java)

}