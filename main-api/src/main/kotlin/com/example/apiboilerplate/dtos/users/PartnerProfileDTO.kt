package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.dtos.address.AddressDTO
import com.example.apiboilerplate.models.user.PartnerProfile
import com.fasterxml.jackson.annotation.JsonProperty

class PartnerProfileDTO(partnerProfile: PartnerProfile): BaseUserProfileDTO(partnerProfile) {

    @JsonProperty("address")
    var address: AddressDTO? = null

    @JsonProperty("paymentEmail")
    var paymentEmail: String? = null

    @JsonProperty("phoneCommercial")
    var phoneCommercial: String? = null

    @JsonProperty("phonePersonal")
    var phonePersonal: String? = null

    @JsonProperty("phoneResidential")
    var phoneResidential: String? = null

    @JsonProperty("rg")
    var rg: String? = null

    @JsonProperty("cpfCnpj")
    var cpfCnpj: String? = null

    init {
        this.paymentEmail = partnerProfile.paymentEmail
        this.phoneCommercial = partnerProfile.phoneCommercial
        this.phonePersonal = partnerProfile.phonePersonal
        this.phoneResidential = partnerProfile.phoneResidential
        this.rg = partnerProfile.rg
        this.cpfCnpj = partnerProfile.cpfCnpj
    }

}