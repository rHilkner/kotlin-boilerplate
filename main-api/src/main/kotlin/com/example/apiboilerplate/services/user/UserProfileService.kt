package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.dtos.users.FullUserDTO
import com.example.apiboilerplate.dtos.users.UserProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.mappers.AppUserMapper
import com.example.apiboilerplate.models.user.*
import com.example.apiboilerplate.repositories.user.AdminProfileRepository
import com.example.apiboilerplate.repositories.user.CustomerProfileRepository
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val appUserService: AppUserService,
    private val adminProfileRepository: AdminProfileRepository,
    private val customerProfileRepository: CustomerProfileRepository,
) {

    companion object { private val log by ApiLogger() }

    private val appUserMapper = AppUserMapper()

    /************************ USER PROFILE REPOSITORIES (ADMIN AND CUSTOMER) ************************/

    fun getUserProfile(appUser: AppUser): UserProfile {
        return when (appUser.role) {
            UserRole.ADMIN -> adminProfileRepository.findByUserId(appUser.userId!!)!!
            UserRole.CUSTOMER -> customerProfileRepository.findByUserId(appUser.userId!!)!!
        }
    }

    fun saveUserProfile(userProfile: UserProfile): UserProfile {
        return when (appUserMapper.getUserRole(userProfile)) {
            UserRole.ADMIN -> adminProfileRepository.save(userProfile as AdminProfile)
            UserRole.CUSTOMER -> customerProfileRepository.save(userProfile as CustomerProfile)
        }
    }

    /************************ USER PROFILE ************************/

    fun updateUserProfile(currentUserProfile: UserProfile, newUserProfile: UserProfileDTO): UserProfileDTO {
        return when (appUserMapper.getUserRole(currentUserProfile)) {
            UserRole.ADMIN -> AdminProfileDTO(currentUserProfile as AdminProfile)
            UserRole.CUSTOMER -> CustomerProfileDTO(currentUserProfile as CustomerProfile)
        }
    }

    /************************ FULL USER ************************/

    fun getCurrentFullUserOrThrow(): FullUser {
        val appUser = appUserService.getCurrentUserOrThrow()
        return getFullUserOrThrow(appUser)
    }

    fun getCurrentFullUserDtoOrThrow(): FullUserDTO {
        val fullUser = getCurrentFullUserOrThrow()
        return appUserMapper.fullUserToFullUserDto(fullUser)
    }

    fun getFullUserDto(email: String, role: UserRole): FullUserDTO? {
        val fullUser = getFullUserOrThrow(email, role)
        return fullUser?.let { appUserMapper.fullUserToFullUserDto(it) }
    }

    fun getFullUserOrThrow(email: String, role: UserRole): FullUser? {
        val appUser = appUserService.getUserByEmailOrThrow(email, role)
        return getFullUserOrThrow(appUser)
    }

    fun getFullUserOrThrow(userId: Long): FullUser {
        val appUser = appUserService.getUserByIdOrThrow(userId)
        return getFullUserOrThrow(appUser)
    }

    fun getFullUserOrThrow(appUser: AppUser): FullUser {
        val userProfile = getUserProfile(appUser)
        return appUserMapper.buildFullUser(appUser, userProfile)
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
        val appUser = this.getFullUserOrThrow(newUserDTO.appUser.userId!!)
        return updateUser(appUser, newUserDTO)
    }

    fun updateUser(currentUser: FullUser, newUserDto: FullUserDTO): FullUserDTO {
        val newAppUser = appUserService.updateUser(currentUser.appUser, newUserDto.appUser)
        val newUserProfile = updateUserProfile(currentUser.userProfile, newUserDto.userProfile)
        return appUserMapper.buildFullUserDto(newAppUser, newUserProfile)
    }

}