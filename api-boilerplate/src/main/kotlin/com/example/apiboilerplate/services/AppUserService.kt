package com.example.apiboilerplate.services

import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.SignUpDTO
import com.example.apiboilerplate.repositories.AppUserRepository
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val validatorService: ValidatorService,
    private val appUserDAO: AppUserRepository,
) {

    private val appUserConverter = AppUserConverter()

    fun signUpUser(signUpDTO: SignUpDTO) {
        // validate sign-up information
        validatorService.validateEmail(signUpDTO.email)
        validatorService.validateEmailAlreadyUsed(signUpDTO.email)
        validatorService.validatePassword(signUpDTO.password)

        // create new user
        val newAppUser = appUserConverter.signUpDtoToAppUser(signUpDTO)
        appUserDAO.save(newAppUser)
    }

    fun getUserByEmail(email: String): AppUserDTO? {
        val appUser = appUserDAO.getAppUserByEmail(email)
        return appUser?.let { appUserConverter.appUserToAppUserDto(it) }
    }

}