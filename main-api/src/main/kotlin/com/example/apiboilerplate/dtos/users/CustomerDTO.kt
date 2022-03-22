package com.example.apiboilerplate.dtos.users

class CustomerDTO(appUserDTO: AppUserDTO, customerProfileDTO: CustomerProfileDTO): FullUserDTO(appUserDTO, customerProfileDTO)