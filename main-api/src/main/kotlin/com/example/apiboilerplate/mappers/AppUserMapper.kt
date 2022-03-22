package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpAdminRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpCustomerRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.users.*
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.user.*
import java.util.*

class AppUserMapper {

    companion object { private val log by ApiLogger() }

    private val adminMapper = AdminMapper()
    private val customerMapper = CustomerMapper()

    fun buildFullUser(appUser: AppUser, userProfile: UserProfile): FullUser {
        return when (appUser.role) {
            UserRole.CUSTOMER -> Customer(appUser, userProfile as CustomerProfile)
            UserRole.ADMIN -> Admin(appUser, userProfile as AdminProfile)
        }
    }

    fun fullUserToFullUserDto(fullUser: FullUser): FullUserDTO {
        return when (fullUser.appUser.role) {
            UserRole.CUSTOMER -> CustomerDTO(appUserToAppUserDto(fullUser.appUser),
                userProfileToUserProfileDto(fullUser.userProfile) as CustomerProfileDTO)
            UserRole.ADMIN -> AdminDTO(appUserToAppUserDto(fullUser.appUser),
                userProfileToUserProfileDto(fullUser.userProfile) as AdminProfileDTO)
        }
    }

    fun appUserToAppUserDto(appUser: AppUser): AppUserDTO {
        log.debug("Converting AppUser to AppUserDTO")
        return AppUserDTO(appUser)
    }

    fun signUpDtoToAppUser(signUpRequestDTO: SignUpRequestDTO, uuid: UUID, userRole: UserRole, passwordHash: String): AppUser {
        log.debug("Converting SignUpRequestDTO to AppUser")
        return AppUser(signUpRequestDTO, uuid, userRole, passwordHash)
    }

    fun signUpDtoToUserProfile(signUpRequestDTO: SignUpRequestDTO, appUser: AppUser): UserProfile {
        return when (appUser.role) {
            UserRole.ADMIN ->
                adminMapper.signUpDtoToAdminProfile(signUpRequestDTO as SignUpAdminRequestDTO, appUser)
            UserRole.CUSTOMER ->
                customerMapper.signUpDtoToCustomerProfile(signUpRequestDTO as SignUpCustomerRequestDTO, appUser)
        }
    }

    private fun userProfileToUserProfileDto(userProfile: UserProfile): UserProfileDTO {
        return when (userProfile) {
            is AdminProfile -> adminMapper.adminProfileToAdminProfileDto(userProfile)
            is CustomerProfile -> customerMapper.customerProfileToCustomerProfileDto(userProfile)
            else -> throw ApiExceptionModule.General.UnexpectedException(
                "UserProfile is not AdminProfile or CustomerProfile")
        }
    }

    fun getUserRole(userProfile: UserProfile): UserRole {
        return when (userProfile) {
            is AdminProfile -> UserRole.ADMIN
            is CustomerProfile -> UserRole.CUSTOMER
            else -> throw ApiExceptionModule.General.UnexpectedException("UserProfile is not of types AdminProfile or CustomerProfile")
        }
    }

    fun buildFullUserDto(appUserDto: AppUserDTO, userProfileDto: UserProfileDTO): FullUserDTO {
        return when (appUserDto.role) {
            UserRole.ADMIN -> AdminDTO(appUserDto, userProfileDto as AdminProfileDTO)
            UserRole.CUSTOMER -> CustomerDTO(appUserDto, userProfileDto as CustomerProfileDTO)
        }
    }

}