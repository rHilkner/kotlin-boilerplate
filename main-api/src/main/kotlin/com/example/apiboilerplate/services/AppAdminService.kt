package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.LoginAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppAdminRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppAdmin
import org.springframework.stereotype.Service

@Service
class AppAdminService(
    private val appUserService: AppUserService
) {

    companion object { private val log by ApiLogger() }

    fun login(loginRequestDTO: LoginRequestDTO): LoginAppAdminResponseDTO {
        return appUserService.login(loginRequestDTO, UserRole.ADMIN) as LoginAppAdminResponseDTO
    }

    fun signUp(signUpAppAdminRequestDTO: SignUpAppAdminRequestDTO): LoginAppAdminResponseDTO {
        return appUserService.signUp(signUpAppAdminRequestDTO, UserRole.ADMIN) as LoginAppAdminResponseDTO
    }

    fun getCurrentAdmin(): AppAdmin {
        return appUserService.getCurrentUser() as AppAdmin
    }

    fun getCurrentAdminDto(): AppAdminDTO {
        return appUserService.getCurrentUserDto() as AppAdminDTO
    }

    fun getAdminDtoByEmail(email: String): AppAdminDTO? {
        return appUserService.getUserDtoByEmail(email, UserRole.ADMIN) as AppAdminDTO?
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentAdmin(newAppAdminDTO: AppAdminDTO): AppAdminDTO {
        return appUserService.updateCurrentUser(newAppAdminDTO) as AppAdminDTO
    }

    fun updateAdmin(newAppAdminDTO: AppAdminDTO): AppAdminDTO {
        return appUserService.updateUser(newAppAdminDTO) as AppAdminDTO
    }

    fun deleteAdmin(adminId: Long) {
        appUserService.deleteUser(adminId, UserRole.ADMIN)
    }

}