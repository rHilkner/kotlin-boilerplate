package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DTOEnum
import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class UserRole: PersistableEnum, DTOEnum {

    FREE {
        override var dbValue = "FREE"
        override var dtoValue = "Free"
    },
    PREMIUM {
        override var dbValue = "PREMIUM"
        override var dtoValue = "Premium"
    };

    class Converter : DbEnumConverter<UserRole>(UserRole::class.java)

}