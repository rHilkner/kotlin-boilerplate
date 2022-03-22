package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.models.CustomerProfile
import com.fasterxml.jackson.annotation.JsonProperty

class CustomerProfileDTO(customerProfile: CustomerProfile): UserProfileDTO() {

    @JsonProperty("phone")
    var phone: String? = null

    @JsonProperty("documentId")
    var documentId: String? = null

    @JsonProperty("address")
    var address: String? = null

    @JsonProperty("addressComplement")
    var addressComplement: String? = null

    init {
        this.phone = customerProfile.phone
        this.documentId = customerProfile.documentId
        this.address = customerProfile.address
        this.addressComplement = customerProfile.addressComplement
    }

}