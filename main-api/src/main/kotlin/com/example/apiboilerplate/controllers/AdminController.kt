package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.services.user.AdminProfileService
import com.example.apiboilerplate.services.user.AppUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val appUserService: AppUserService,
    private val adminService: AdminProfileService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<LoginAdminResponseDTO>> {
        return response(adminService.login(loginRequestDTO, UserRole.ADMIN), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid signUpRequestDTO: SignUpRequestDTO): ResponseEntity<ResponsePayload<LoginAdminResponseDTO>> {
        return response(adminService.signUp(signUpRequestDTO, UserRole.ADMIN), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forgotPassword(email, UserRole.ADMIN), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequestDTO: ResetPasswordRequestDTO): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.resetPassword(resetPasswordRequestDTO), HttpStatus.OK)
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
    fun getCurrentUser(): ResponseEntity<ResponsePayload<AdminProfileDTO>> {
        return response(adminService.getCurrentDtoOrThrow(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_admin_by_email")
    fun getAdminByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AdminProfileDTO>> {
        val adminProfileDto = adminService.getDtoByEmailOrThrow(email)
        return response(adminProfileDto, HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_all_users")
    fun getAllUsers(@RequestParam pageNumber: Int, @RequestParam pageSize: Int): ResponseEntity<ResponsePayload<List<AdminProfileDTO>>> {
        return response(adminService.getAllPaginated(pageNumber, pageSize), HttpStatus.OK)
    }

    // POST ENDPOINTS

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newAdminProfileDTO: AdminProfileDTO): ResponseEntity<Any> {
        adminService.updateCurrentOrThrow(newAdminProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_admin")
    @Transactional
    fun updateAdmin(@RequestBody newAdminProfileDTO: AdminProfileDTO): ResponseEntity<Any> {
        adminService.update(newAdminProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_admin")
    @Transactional
    fun deleteAdmin(@RequestParam userUuid: UUID): ResponseEntity<Any> {
        adminService.softDelete(userUuid)
        return response(HttpStatus.OK)
    }

}