package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class Permission: PersistableEnum {

    RESET_PASSWORD {
        override var dbValue = "A"
    };

    class Converter : DbEnumConverter<Permission>(Permission::class.java)

}