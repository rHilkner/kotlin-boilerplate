package com.example.apiboilerplate.dtos.users

class AdminDTO(appUserDTO: AppUserDTO, adminProfileDTO: AdminProfileDTO): FullUserDTO(appUserDTO, adminProfileDTO)