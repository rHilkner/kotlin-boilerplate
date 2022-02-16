package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.ResetPasswordRequest
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppAdminService
import com.example.apiboilerplate.services.AppCustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AppAdminController(
    private val appAdminService: AppAdminService,
    private val appCustomerService: AppCustomerService
): AbstractController() {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    fun signUp(@RequestBody @Valid adminSignUpRequestDTO: AdminSignUpRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.signUp(adminSignUpRequestDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/forgot_password")
    fun forgotPassword(@RequestBody email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.forgotPassword(email), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/reset_password")
    fun resetPassword(@RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.resetPassword(resetPasswordRequest), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @SecuredPermission([Permission.RESET_PASSWORD])
    @PostMapping("/force_reset_password")
    fun forceResetPassword(@RequestBody newPassword: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.forceResetPassword(newPassword), HttpStatus.OK)
    }

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

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_customer_by_email")
    fun getCustomerByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        val appUserDto = appCustomerService.getCustomerDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_current_user")
    fun updateCurrentUser(@RequestBody newAppAdminDTO: AppAdminDTO): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.updateCurrentAdmin(newAppAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_customer")
    fun updateCustomer(@RequestBody newAppCustomerDTO: AppCustomerDTO): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        return response(appCustomerService.updateCustomer(newAppCustomerDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_admin")
    fun updateAdmin(@RequestBody newAppAdminDTO: AppAdminDTO): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.updateAdmin(newAppAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_customer")
    fun deleteCustomer(@RequestBody customerId: Long): ResponseEntity<ResponsePayload<Any?>> {
        return response(appCustomerService.deleteCustomer(customerId), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_admin")
    fun deleteAdmin(@RequestBody adminId: Long): ResponseEntity<ResponsePayload<Any?>> {
        return response(appAdminService.deleteAdmin(adminId), HttpStatus.OK)
    }

}