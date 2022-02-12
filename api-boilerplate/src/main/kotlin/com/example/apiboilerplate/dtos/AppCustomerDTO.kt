package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppCustomerDTO {

    constructor(appCustomer: AppCustomer) {
        ObjectUtil.copyProps(this, appCustomer)
    }

    @JsonProperty("customerId")
    var customerId: Long = 0

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("role")
    var role: UserRole = UserRole.CUSTOMER

}