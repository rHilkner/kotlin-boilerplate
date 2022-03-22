package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.dtos.users.FullUserDTO
import com.example.apiboilerplate.dtos.users.UserProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.*
import com.example.apiboilerplate.repositories.AdminProfileRepository
import com.example.apiboilerplate.repositories.CustomerProfileRepository
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val appUserService: AppUserService,
    private val adminProfileRepository: AdminProfileRepository,
    private val customerProfileRepository: CustomerProfileRepository,
) {

    companion object { private val log by ApiLogger() }

    private val appUserConverter = AppUserConverter()

    /************************ USER PROFILE REPOSITORIES (ADMIN AND CUSTOMER) ************************/

    fun getUserProfile(appUser: AppUser): UserProfile {
        return when (appUser.role) {
            UserRole.ADMIN -> adminProfileRepository.findByUserId(appUser.userId!!)!!
            UserRole.CUSTOMER -> customerProfileRepository.findByUserId(appUser.userId!!)!!
        }
    }

    fun saveUserProfile(userProfile: UserProfile): UserProfile {
        return when (appUserConverter.getUserRole(userProfile)) {
            UserRole.ADMIN -> adminProfileRepository.save(userProfile as AdminProfile)
            UserRole.CUSTOMER -> customerProfileRepository.save(userProfile as CustomerProfile)
        }
    }

    /************************ FULL USER ************************/

    fun getCurrentFullUserOrThrow(): FullUser {
        val appUser = appUserService.getCurrentUserOrThrow()
        return getFullUser(appUser)
    }

    fun getCurrentFullUserDtoOrThrow(): FullUserDTO {
        val fullUser = getCurrentFullUserOrThrow()
        return appUserConverter.fullUserToFullUserDto(fullUser)
    }

    fun getFullUserDto(email: String, role: UserRole): FullUserDTO? {
        val fullUser = getFullUser(email, role)
        return fullUser?.let { appUserConverter.fullUserToFullUserDto(it) }
    }

    fun getFullUser(email: String, role: UserRole): FullUser? {
        val appUser = appUserService.getUserByEmailOrThrow(email, role)
        return getFullUser(appUser)
    }

    fun getFullUser(userId: Long): FullUser {
        val appUser = appUserService.getUserByIdOrThrow(userId)
        return getFullUser(appUser)
    }

    fun getFullUser(appUser: AppUser): FullUser {
        val userProfile = getUserProfile(appUser)
        return appUserConverter.buildFullUser(appUser, userProfile)
    }

    fun updateCurrentUser(newUserDTO: FullUserDTO): FullUserDTO {
        val fullUser = this.getCurrentFullUserOrThrow()

        // Verify if current user is trying to update some user other than himself
        if (fullUser.appUser.userId != newUserDTO.appUser.userId) {
            log.error("User ${fullUser.appUser.userId} does not have enough privileges to update user with id ${newUserDTO.appUser.userId}")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(
                "User ${fullUser.appUser.userId} does not have enough privileges to update user with id ${newUserDTO.appUser.userId}")
        }
        return updateUser(fullUser, newUserDTO)

    }

    fun updateUser(newUserDTO: FullUserDTO): FullUserDTO {
        if (newUserDTO.appUser.userId == null) throw ApiExceptionModule.General.NullPointer("userDTO.userId")
        val appUser = this.getFullUser(newUserDTO.appUser.userId!!)
        return updateUser(appUser, newUserDTO)
    }

    /************************ USER PROFILE ************************/

    fun updateUser(currentUser: FullUser, newUserDto: FullUserDTO): FullUserDTO {
        val newAppUser = appUserService.updateUser(currentUser.appUser, newUserDto.appUser)
        val newUserProfile = updateUserProfile(currentUser.userProfile, newUserDto.userProfile)
        return appUserConverter.buildFullUserDto(newAppUser, newUserProfile)
    }

    fun updateUserProfile(currentUserProfile: UserProfile, newUserProfile: UserProfileDTO): UserProfileDTO {
        return when (appUserConverter.getUserRole(currentUserProfile)) {
            UserRole.ADMIN -> AdminProfileDTO(currentUserProfile as AdminProfile)
            UserRole.CUSTOMER -> CustomerProfileDTO(currentUserProfile as CustomerProfile)
        }
    }

}