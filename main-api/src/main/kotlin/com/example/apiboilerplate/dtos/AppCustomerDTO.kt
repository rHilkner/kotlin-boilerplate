package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppCustomerDTO(appCustomer: AppCustomer): AppUserDTO(appCustomer) {

    init {
        ObjectUtil.copyProps(appCustomer, this)
    }

    @JsonProperty("customerId")
    var customerId: Long? = super.userId

    @JsonProperty("phone")
    var phone: String? = null

    @JsonProperty("documentId")
    var documentId: String? = null

    @JsonProperty("address")
    var address: String? = null

    @JsonProperty("addressComplement")
    var addressComplement: String? = null

}