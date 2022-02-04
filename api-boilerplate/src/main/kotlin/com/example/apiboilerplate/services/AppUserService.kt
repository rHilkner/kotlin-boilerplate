package com.example.apiboilerplate.services

import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.SignUpDTO
import com.example.apiboilerplate.repositories.AppUserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val validatorService: ValidatorService,
    private val appUserDAO: AppUserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val appUserConverter = AppUserConverter()

    fun signUpUser(signUpDTO: SignUpDTO) {
        // Validate and hide password
        validatorService.validatePassword(signUpDTO.password)
        signUpDTO.password = encodePassword(signUpDTO.password)
        // Validate user information
        validatorService.validateEmail(signUpDTO.email)
        validatorService.validateEmailAlreadyUsed(signUpDTO.email)
        // Create new user
        val newAppUser = appUserConverter.signUpDtoToAppUser(signUpDTO)
        appUserDAO.save(newAppUser)
    }

    private fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun getUserByEmail(email: String): AppUserDTO? {
        val appUser = appUserDAO.getAppUserByEmail(email)
        return appUser?.let { appUserConverter.appUserToAppUserDto(it) }
    }

}