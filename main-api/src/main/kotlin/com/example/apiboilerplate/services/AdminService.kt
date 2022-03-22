package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.LoginAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpAdminRequestDTO
import com.example.apiboilerplate.dtos.users.AdminDTO
import com.example.apiboilerplate.enums.UserRole
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val appUserService: AppUserService,
    private val userProfileService: UserProfileService
) {

    companion object { private val log by ApiLogger() }

    /************************ AUTH & HIGH SECURITY ************************/

    fun login(loginRequestDTO: LoginRequestDTO): LoginAppAdminResponseDTO {
        return appUserService.login(loginRequestDTO, UserRole.ADMIN) as LoginAppAdminResponseDTO
    }

    fun signUp(signUpAdminRequestDTO: SignUpAdminRequestDTO): LoginAppAdminResponseDTO {
        return appUserService.signUp(signUpAdminRequestDTO, UserRole.ADMIN) as LoginAppAdminResponseDTO
    }

    /************************ CRUD ************************/

    fun getCurrentAdminDto(): AdminDTO {
        return userProfileService.getCurrentFullUserDtoOrThrow() as AdminDTO
    }

    fun getAdminDtoByEmail(email: String): AdminDTO? {
        return userProfileService.getFullUserDto(email, UserRole.ADMIN) as AdminDTO?
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentAdmin(newAdminDTO: AdminDTO): AdminDTO {
        return userProfileService.updateCurrentUser(newAdminDTO) as AdminDTO
    }

    fun updateAdmin(newAdminDTO: AdminDTO): AdminDTO {
        return userProfileService.updateUser(newAdminDTO) as AdminDTO
    }

    fun deleteAdmin(adminId: Long) {
        appUserService.deleteUser(adminId, UserRole.ADMIN)
    }

}