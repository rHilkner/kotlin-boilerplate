package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.models.user.CustomerProfile
import com.fasterxml.jackson.annotation.JsonProperty

class CustomerProfileDTO(customerProfile: CustomerProfile): BaseUserProfileDTO(customerProfile) {

    @JsonProperty("phone")
    var phone: String? = null

    @JsonProperty("documentId")
    var documentId: String? = null

    init {
        this.phone = customerProfile.phone
        this.documentId = customerProfile.documentId
    }

}