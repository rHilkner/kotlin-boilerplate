package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.dtos.auth.BaseLoginResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.users.BaseUserProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.mappers.user.UserProfileMapper
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.BaseUserProfile
import java.util.*

interface IUserProfileService<Model: BaseUserProfile, Dto: BaseUserProfileDTO, LoginResponseDto: BaseLoginResponseDTO> {

    val appUserService: AppUserService
    val profileMapper: UserProfileMapper<Model, Dto, LoginResponseDto>

    /************************ AUTH & HIGH SECURITY ************************/

    fun login(loginRequestDTO: LoginRequestDTO, userRole: UserRole): LoginResponseDto {
        // Login app user - will throw error in case of auth failure
        val appUser = appUserService.login(loginRequestDTO, userRole)

        // Get user with profile
        val userProfile = getOrThrow(appUser)

        // If no error was thrown, return response dto
        val apiSession = ApiSessionContext.getCurrentApiCallContext().apiSession!!
        return profileMapper.buildLoginResponseOrThrow(userProfile, apiSession) as LoginResponseDto
    }

    fun signUp(signUpRequestDTO: SignUpRequestDTO, userRole: UserRole): LoginResponseDto {
        // Sign up to app user - will throw error in case of auth failure
        val appUser = appUserService.signUp(signUpRequestDTO, userRole)

        // Create user profile
        var userProfile = profileMapper.createUserProfile(appUser)
        userProfile = save(userProfile)

        // If no error was thrown, return response dto
        val apiSession = ApiSessionContext.getCurrentApiCallContext().apiSession!!
        return profileMapper.buildLoginResponseOrThrow(userProfile, apiSession) as LoginResponseDto
    }

    // CURRENT USER
    fun getCurrentDtoOrThrow(): Dto
    fun getCurrentOrThrow(): Model

    // SAVE
    fun save(userProfile: Model, shouldSaveAppUser: Boolean = true): Model

    // GET
    fun getByUserIdOrThrow(userId: Long): Model
    fun getDtoByEmailOrThrow(email: String): Dto
    fun getByEmailOrThrow(email: String): Model
    fun getOrThrow(appUser: AppUser): Model
    fun getAllPaginated(pageNumber: Int, pageSize: Int): List<Dto>

    // UPDATE
    /**
     * updatable fields: profile.appUser.name, profile.documents, profile.phones, profile.photos, profile.addresses
     * pretty much all non-sensitive and app-core information
     */
    fun updateCurrentOrThrow(dto: Dto): Model
    fun update(dto: Dto): Model // update based on dto.userId
    fun update(current: Model, dto: Dto): Model

    // DELETE
    fun softDelete(userUuid: UUID)

}