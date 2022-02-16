package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DTOEnum
import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class UserRole: PersistableEnum, DTOEnum {

    CUSTOMER {
        override var dbValue = "CUSTOMER"
        override var dtoValue = "CUSTOMER"
    },
    ADMIN {
        override var dbValue = "ADMIN"
        override var dtoValue = "ADMIN"
    };

    class Converter : DbEnumConverter<UserRole>(UserRole::class.java)

}