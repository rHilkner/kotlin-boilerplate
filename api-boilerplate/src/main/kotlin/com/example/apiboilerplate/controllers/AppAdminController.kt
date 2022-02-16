package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppAdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AppAdminController(
    private val appAdminService: AppAdminService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid adminSignUpRequestDTO: AdminSignUpRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.signUp(adminSignUpRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.forgotPassword(email), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.resetPassword(resetPasswordRequest), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @SecuredPermission([Permission.RESET_PASSWORD])
    @PostMapping("/force_reset_password")
    @Transactional
    fun forceResetPassword(@RequestBody req: ForceResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.forceResetPassword(req.newPassword), HttpStatus.OK)
    }

    // GET ENDPOINTS

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_current_user")
    fun getCurrentUser(): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.getCurrentAdminDto(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_admin_by_email")
    fun getAdminByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        val appUserDto = appAdminService.getAdminDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

    // POST ENDPOINTS

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newAppAdminDTO: AppAdminDTO): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.updateCurrentAdmin(newAppAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_admin")
    @Transactional
    fun updateAdmin(@RequestBody newAppAdminDTO: AppAdminDTO): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.updateAdmin(newAppAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_admin")
    @Transactional
    fun deleteAdmin(@RequestParam adminId: Long): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.deleteAdmin(adminId), HttpStatus.OK)
    }

}