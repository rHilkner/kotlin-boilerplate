package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.AdminDTO
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AdminService
import com.example.apiboilerplate.services.AppUserService
import com.example.apiboilerplate.services.CustomerService
import com.example.apiboilerplate.services.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/admin")
class AppAdminController(
    private val appUserService: AppUserService,
    private val adminService: AdminService,
    private val customerService: CustomerService,
    private val userProfileService: UserProfileService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<LoginAppAdminResponseDTO>> {
        return response(adminService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid signUpAdminRequestDTO: SignUpAdminRequestDTO): ResponseEntity<ResponsePayload<LoginAppAdminResponseDTO>> {
        return response(adminService.signUp(signUpAdminRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forgotPassword(email, UserRole.ADMIN), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.resetPassword(resetPasswordRequest), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @SecuredPermission([Permission.RESET_PASSWORD])
    @PostMapping("/force_reset_password")
    @Transactional
    fun forceResetPassword(@RequestBody req: ForceResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forceResetPassword(req.newPassword), HttpStatus.OK)
    }

    // GET ENDPOINTS

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_current_user")
    fun getCurrentUser(): ResponseEntity<ResponsePayload<AdminDTO>> {
        return response(adminService.getCurrentAdminDto(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_admin_by_email")
    fun getAdminByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AdminDTO>> {
        val appUserDto = adminService.getAdminDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

    @SecuredRole([UserRole.CUSTOMER])
    @GetMapping("/download_current_user_profile_image")
    fun getCurrentUserProfileImage(): ByteArray? {
        return customerService.downloadCurrentUserProfileImage()
    }

    // POST ENDPOINTS

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newAdminDTO: AdminDTO): ResponseEntity<ResponsePayload<AdminDTO>> {
        return response(adminService.updateCurrentAdmin(newAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_admin")
    @Transactional
    fun updateAdmin(@RequestBody newAdminDTO: AdminDTO): ResponseEntity<ResponsePayload<AdminDTO>> {
        return response(adminService.updateAdmin(newAdminDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_admin")
    @Transactional
    fun deleteAdmin(@RequestParam adminId: Long): ResponseEntity<ResponsePayload<Any?>> {
        return response(adminService.deleteAdmin(adminId), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping(
        path = ["/upload_profile_image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Transactional
    fun uploadProfileImage(@RequestParam file: MultipartFile): ResponseEntity<ResponsePayload<Any?>> {
        return response(customerService.uploadProfileImage(file), HttpStatus.OK)
    }

}