package com.example.apiboilerplate.enums.converters

import com.fasterxml.jackson.annotation.JsonValue

interface DTOEnum {
    var dtoValue: String

    @JsonValue
    fun getJsonValue(): String {
        return this.dtoValue
    }

}