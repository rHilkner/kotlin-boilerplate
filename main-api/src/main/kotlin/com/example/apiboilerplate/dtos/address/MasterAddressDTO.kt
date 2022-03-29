package com.example.apiboilerplate.dtos.address

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.models.address.MasterAddress
import com.fasterxml.jackson.annotation.JsonProperty

class MasterAddressDTO(masterAddress: MasterAddress): IAppDTO {

    @JsonProperty("masterAddressId")
    var masterAddressId: String? = null

    @JsonProperty("googlePlaceId")
    var googlePlaceId: String? = null

    @JsonProperty("zip")
    var zip: String? = null

    @JsonProperty("addressLine")
    var addressLine: String? = null

    @JsonProperty("addressNumber")
    var addressNumber: String? = null

    @JsonProperty("city")
    var city: String? = null

    @JsonProperty("state")
    var state: String? = null

    @JsonProperty("country")
    var country: String? = null

    @JsonProperty("latitude")
    var latitude: Double? = null

    @JsonProperty("longitude")
    var longitude: Double? = null

}
