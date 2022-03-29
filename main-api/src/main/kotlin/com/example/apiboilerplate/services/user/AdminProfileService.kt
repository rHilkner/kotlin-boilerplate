package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.dtos.auth.LoginAdminResponseDTO
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.mappers.user.AdminProfileMapper
import com.example.apiboilerplate.models.user.AdminProfile
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.repositories.user.AdminProfileRepository
import com.example.apiboilerplate.validators.SessionValidator
import org.springframework.stereotype.Service
import java.util.*

@Service
class AdminProfileService(
    override val appUserService: AppUserService,
    private val adminProfileRepository: AdminProfileRepository,
    private val sessionValidator: SessionValidator
): IUserProfileService<AdminProfile, AdminProfileDTO, LoginAdminResponseDTO> {

    override val profileMapper = AdminProfileMapper()

    override fun save(userProfile: AdminProfile, shouldSaveAppUser: Boolean): AdminProfile {
        if (shouldSaveAppUser) {
            userProfile.appUser = appUserService.save(userProfile.appUser)
        }
        return adminProfileRepository.save(userProfile)
    }

    override fun getCurrentDtoOrThrow(): AdminProfileDTO {
        return AdminProfileDTO(getCurrentOrThrow())
    }

    override fun getCurrentOrThrow(): AdminProfile {
        val user = appUserService.getCurrentUserOrThrow()
        return getOrThrow(user)
    }

    override fun getOrThrow(appUser: AppUser): AdminProfile {
        return adminProfileRepository.findByUserId(appUser.userId!!)
            ?: throw ApiExceptionModule.User.UserNotFoundException(appUser.userId)
    }

    override fun getAllPaginated(pageNumber: Int, pageSize: Int): List<AdminProfileDTO> {
        return adminProfileRepository.findAll().map { AdminProfileDTO(it) }.toList()
    }

    override fun updateCurrentOrThrow(dto: AdminProfileDTO): AdminProfile {
        val userProfile = getCurrentOrThrow()
        return update(userProfile, dto)
    }

    /**
     * For admins, only name can be updated in profile-information
     */
    override fun update(current: AdminProfile, dto: AdminProfileDTO): AdminProfile {
        current.appUser.name = dto.appUser.name
        return save(current)
    }

    override fun getByUserIdOrThrow(userId: Long): AdminProfile {
        return adminProfileRepository.findByUserId(userId)
            ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    override fun update(dto: AdminProfileDTO): AdminProfile {
        val currentProfile = getByUserIdOrThrow(dto.appUser.userId!!)
        return update(currentProfile, dto)
    }

    override fun getByEmailOrThrow(email: String): AdminProfile {
        val appUser = appUserService.getUserByEmailOrThrow(email, UserRole.ADMIN)
        return getOrThrow(appUser)
    }

    override fun getDtoByEmailOrThrow(email: String): AdminProfileDTO {
        val userProfile = this.getByEmailOrThrow(email)
        return AdminProfileDTO(userProfile)
    }

    override fun softDelete(userUuid: UUID) {
        sessionValidator.validateCurrentUserRole(UserRole.ADMIN)
        appUserService.softDeleteUser(userUuid)
    }

}