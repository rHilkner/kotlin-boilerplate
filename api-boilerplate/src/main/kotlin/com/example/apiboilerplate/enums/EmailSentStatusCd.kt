package com.example.apiboilerplate.enums

import com.example.apiboilerplate.enums.converters.DbEnumConverter
import com.example.apiboilerplate.enums.converters.PersistableEnum

enum class EmailSentStatusCd: PersistableEnum {

    OK {
        override var dbValue = "OK"
    },
    FAIL {
        override var dbValue = "FAIL"
    };

    class Converter : DbEnumConverter<EmailSentStatusCd>(EmailSentStatusCd::class.java)

}