package com.example.apiboilerplate.dtos.address

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.models.address.Address
import com.fasterxml.jackson.annotation.JsonProperty

class AddressDTO(address: Address): IAppDTO {

    @JsonProperty("")
    var addressComplement: String? = null

    @JsonProperty("")
    var addressReference: String? = null

    @JsonProperty("")
    var masterAddress: MasterAddressDTO? = null

    init {
        this.addressComplement = address.addressComplement
        this.addressReference = address.addressReference
        this.masterAddress = address.masterAddress?.let { MasterAddressDTO(it) }
    }

}
