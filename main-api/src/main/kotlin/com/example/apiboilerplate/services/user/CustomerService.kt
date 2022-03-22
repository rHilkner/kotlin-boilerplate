package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.LoginAppCustomerResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.dtos.users.CustomerDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.enums.AppImageType
import com.example.apiboilerplate.enums.AppPaths
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.user.Customer
import com.example.apiboilerplate.services.ImageService
import com.example.apiboilerplate.services.base.StorageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CustomerService(
    private val appUserService: AppUserService,
    private val userProfileService: UserProfileService,
    private val imageService: ImageService,
    private val storageService: StorageService
) {

    companion object { private val log by ApiLogger() }

    /************************ AUTH & HIGH SECURITY ************************/

    fun login(loginRequestDTO: LoginRequestDTO): LoginAppCustomerResponseDTO {
        return appUserService.login(loginRequestDTO, UserRole.CUSTOMER) as LoginAppCustomerResponseDTO
    }

    fun signUp(signUpAppCustomerRequestDTO: SignUpAppCustomerRequestDTO): LoginAppCustomerResponseDTO {
        return appUserService.signUp(signUpAppCustomerRequestDTO, UserRole.CUSTOMER) as LoginAppCustomerResponseDTO
    }

    fun getCurrentCustomerDto(): CustomerDTO {
        return userProfileService.getCurrentFullUserDtoOrThrow() as CustomerDTO
    }

    /************************ CRUD ************************/

    fun getCustomerDtoByEmail(email: String): CustomerProfileDTO? {
        return appUserService.getUserDtoByEmail(email, UserRole.CUSTOMER) as CustomerProfileDTO?
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentCustomer(newCustomerDTO: CustomerDTO): CustomerDTO {
        return userProfileService.updateCurrentUser(newCustomerDTO) as CustomerDTO
    }

    fun updateCustomer(newCustomerDTO: CustomerDTO): CustomerDTO {
        return userProfileService.updateUser(newCustomerDTO) as CustomerDTO
    }

    fun deleteCustomer(customerId: Long) {
        appUserService.deleteUser(customerId, UserRole.CUSTOMER)
    }

    /************************ FEATURES ************************/

    fun uploadProfileImage(file: MultipartFile) {
        // Save image to server's internal storage
        val customer = userProfileService.getCurrentFullUserOrThrow() as Customer
        log.info("Saving user [${customer.appUser.userId}] profile image")
        val fileDirectory = AppPaths.getProfileImageDirectory(customer.appUser.userId!!, customer.appUser.role)
        val fileName = "profile_image.jpg"
        val optimizedImage = imageService.formatImage(file.bytes, AppImageType.PROFILE)
        storageService.saveImage(optimizedImage, fileDirectory, fileName)

        // Save profile-image path to user-profile
        val fullPath = fileDirectory+fileName
        customer.userProfile.profileImagePath = fullPath
        userProfileService.saveUserProfile(customer.userProfile)
        log.debug("User [${customer.appUser.userId}] profile picture saved successfully at [$fullPath]")
    }

    fun downloadCurrentUserProfileImage(): ByteArray? {
        val customer = userProfileService.getCurrentFullUserOrThrow() as Customer
        log.info("Downloading user [${customer.appUser.userId}] profile image")
        return customer.userProfile.profileImagePath?.let { storageService.downloadImage(it) }
    }

}