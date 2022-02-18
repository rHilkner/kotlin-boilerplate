package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppCustomerDTO(appCustomer: AppCustomer) {

    init {
        ObjectUtil.copyProps(appCustomer, this)
    }

    @JsonProperty("customerId")
    var customerId: Long? = null

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("profileImagePath")
    lateinit var profileImagePath: String

    @JsonProperty("phone")
    var phone: String? = null

    @JsonProperty("documentId")
    var documentId: String? = null

    @JsonProperty("address")
    var address: String? = null

    @JsonProperty("addressComplement")
    var addressComplement: String? = null

    @JsonProperty("role")
    var role: UserRole = UserRole.CUSTOMER

}