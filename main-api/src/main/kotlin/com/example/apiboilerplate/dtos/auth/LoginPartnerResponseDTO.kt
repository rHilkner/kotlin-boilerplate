package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.PartnerProfileDTO

class LoginPartnerResponseDTO(
    partner: PartnerProfileDTO,
    apiSession: ApiSessionResponseDTO
) : BaseLoginResponseDTO(partner, apiSession)