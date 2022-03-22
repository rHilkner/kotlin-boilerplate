package com.example.apiboilerplate.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty

class SignUpCustomerRequestDTO(

    email: String,
    name: String,
    password: String,

    @JsonProperty("phone")
    val phone: String?,

    @JsonProperty("documentId")
    val documentId: String?,

    @JsonProperty("address")
    val address: String?,

    @JsonProperty("addressComplement")
    val addressComplement: String?

) : SignUpRequestDTO(email, name, password)