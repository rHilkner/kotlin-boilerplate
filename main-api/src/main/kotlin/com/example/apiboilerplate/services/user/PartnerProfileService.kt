package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.dtos.auth.LoginPartnerResponseDTO
import com.example.apiboilerplate.dtos.users.PartnerProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.mappers.user.PartnerProfileMapper
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.PartnerProfile
import com.example.apiboilerplate.repositories.user.PartnerProfileRepository
import com.example.apiboilerplate.services.ImageService
import com.example.apiboilerplate.services.base.StorageService
import com.example.apiboilerplate.validators.SessionValidator
import org.springframework.stereotype.Service
import java.util.*

@Service
final class PartnerProfileService(
    override val appUserService: AppUserService,
    override val imageService: ImageService,
    override val storageService: StorageService,
    private val partnerProfileRepository: PartnerProfileRepository,
    private val sessionValidator: SessionValidator
): IUserProfileService<PartnerProfile, PartnerProfileDTO, LoginPartnerResponseDTO>,
    IProfileImageService<PartnerProfile, PartnerProfileDTO> {

    override val profileService = this
    override val profileMapper = PartnerProfileMapper()

    override fun save(userProfile: PartnerProfile, shouldSaveAppUser: Boolean): PartnerProfile {
        if (shouldSaveAppUser) {
            userProfile.appUser = appUserService.save(userProfile.appUser)
        }
        return partnerProfileRepository.save(userProfile)
    }

    override fun getCurrentDtoOrThrow(): PartnerProfileDTO {
        return PartnerProfileDTO(getCurrentOrThrow())
    }

    override fun getCurrentOrThrow(): PartnerProfile {
        val user = appUserService.getCurrentUserOrThrow()
        return getOrThrow(user)
    }

    override fun getOrThrow(appUser: AppUser): PartnerProfile {
        return partnerProfileRepository.findByUserId(appUser.userId!!)!!
    }

    override fun getAllPaginated(pageNumber: Int, pageSize: Int): List<PartnerProfileDTO> {
        return partnerProfileRepository.findAll().map { PartnerProfileDTO(it) }.toList()
    }

    override fun updateCurrentOrThrow(dto: PartnerProfileDTO): PartnerProfile {
        val userProfile = getCurrentOrThrow()
        return update(userProfile, dto)
    }

    /**
     * For partners, only name can be updated in profile-information
     */
    override fun update(current: PartnerProfile, dto: PartnerProfileDTO): PartnerProfile {
        current.appUser.name = dto.appUser.name
        return save(current)
    }

    override fun getByUserIdOrThrow(userId: Long): PartnerProfile {
        return partnerProfileRepository.findByUserId(userId)
            ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    override fun update(dto: PartnerProfileDTO): PartnerProfile {
        val currentProfile = getByUserIdOrThrow(dto.appUser.userId!!)
        return update(currentProfile, dto)
    }

    override fun getByEmailOrThrow(email: String): PartnerProfile {
        val appUser = appUserService.getUserByEmailOrThrow(email, UserRole.PARTNER)
        return getOrThrow(appUser)
    }

    override fun getDtoByEmailOrThrow(email: String): PartnerProfileDTO {
        val userProfile = this.getByEmailOrThrow(email)
        return PartnerProfileDTO(userProfile)
    }

    override fun softDelete(userUuid: UUID) {
        sessionValidator.validateCurrentUserRole(UserRole.ADMIN)
        appUserService.softDeleteUser(userUuid)
    }

}