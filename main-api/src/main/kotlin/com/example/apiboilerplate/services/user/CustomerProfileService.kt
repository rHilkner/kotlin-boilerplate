package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.dtos.auth.LoginCustomerResponseDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.mappers.user.CustomerProfileMapper
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.CustomerProfile
import com.example.apiboilerplate.repositories.user.CustomerProfileRepository
import com.example.apiboilerplate.services.ImageService
import com.example.apiboilerplate.services.base.StorageService
import com.example.apiboilerplate.validators.SessionValidator
import org.springframework.stereotype.Service
import java.util.*

@Service
final class CustomerProfileService(
    override val appUserService: AppUserService,
    override val imageService: ImageService,
    override val storageService: StorageService,
    private val customerProfileRepository: CustomerProfileRepository,
    private val sessionValidator: SessionValidator
): IUserProfileService<CustomerProfile, CustomerProfileDTO, LoginCustomerResponseDTO>,
    IProfileImageService<CustomerProfile, CustomerProfileDTO> {

    override val profileService = this
    override val profileMapper = CustomerProfileMapper()

    /************************** USER PROFILE SERVICE **************************/

    override fun save(userProfile: CustomerProfile, shouldSaveAppUser: Boolean): CustomerProfile {
        if (shouldSaveAppUser) {
            userProfile.appUser = appUserService.save(userProfile.appUser)
        }
        return customerProfileRepository.save(userProfile)
    }

    override fun getCurrentDtoOrThrow(): CustomerProfileDTO {
        return CustomerProfileDTO(getCurrentOrThrow())
    }

    override fun getCurrentOrThrow(): CustomerProfile {
        val user = appUserService.getCurrentUserOrThrow()
        return getOrThrow(user)
    }

    override fun getOrThrow(appUser: AppUser): CustomerProfile {
        return customerProfileRepository.findByUserId(appUser.userId!!)!!
    }

    override fun getAllPaginated(pageNumber: Int, pageSize: Int): List<CustomerProfileDTO> {
        return customerProfileRepository.findAll().map { CustomerProfileDTO(it) }.toList()
    }

    override fun updateCurrentOrThrow(dto: CustomerProfileDTO): CustomerProfile {
        val userProfile = getCurrentOrThrow()
        return update(userProfile, dto)
    }

    /**
     * For customers, only name can be updated in profile-information
     */
    override fun update(current: CustomerProfile, dto: CustomerProfileDTO): CustomerProfile {
        current.appUser.name = dto.appUser.name
        current.phone = dto.phone
        current.documentId = dto.documentId
        return save(current)
    }

    override fun getByUserIdOrThrow(userId: Long): CustomerProfile {
        return customerProfileRepository.findByUserId(userId)
            ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    override fun update(dto: CustomerProfileDTO): CustomerProfile {
        val currentProfile = getByUserIdOrThrow(dto.appUser.userId!!)
        return update(currentProfile, dto)
    }

    override fun getByEmailOrThrow(email: String): CustomerProfile {
        val appUser = appUserService.getUserByEmailOrThrow(email, UserRole.CUSTOMER)
        return getOrThrow(appUser)
    }

    override fun getDtoByEmailOrThrow(email: String): CustomerProfileDTO {
        val userProfile = this.getByEmailOrThrow(email)
        return CustomerProfileDTO(userProfile)
    }

    override fun softDelete(userId: UUID) {
        sessionValidator.validateCurrentUserRole(UserRole.ADMIN)
        appUserService.softDeleteUser(userId)
    }

    /************************** PROFILE IMAGE SERVICE **************************/
    // Nothing to override

}