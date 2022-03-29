package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.services.user.AppUserService
import com.example.apiboilerplate.services.user.CustomerProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/customer")
class CustomerController(
    private val customerService: CustomerProfileService,
    private val appUserService: AppUserService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<LoginCustomerResponseDTO>> {
        return response(customerService.login(loginRequestDTO, UserRole.CUSTOMER), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid signUpRequestDTO: SignUpRequestDTO): ResponseEntity<ResponsePayload<LoginCustomerResponseDTO>> {
        return response(customerService.signUp(signUpRequestDTO, UserRole.CUSTOMER), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forgotPassword(email, UserRole.CUSTOMER), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequestDTO: ResetPasswordRequestDTO): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.resetPassword(resetPasswordRequestDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @SecuredPermission([Permission.RESET_PASSWORD])
    @PostMapping("/force_reset_password")
    @Transactional
    fun forceResetPassword(@RequestBody req: ForceResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forceResetPassword(req.newPassword), HttpStatus.OK)
    }

    // GET ENDPOINTS

    @SecuredRole([UserRole.CUSTOMER])
    @GetMapping("/get_current_user")
    fun getCurrentUser(): ResponseEntity<ResponsePayload<CustomerProfileDTO>> {
        return response(customerService.getCurrentDtoOrThrow(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @GetMapping("/download_current_user_profile_image")
    fun getCurrentUserProfileImage(): ByteArray? {
        return customerService.downloadCurrentUserProfileImage()
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_all_users")
    fun getAllUsers(@RequestParam pageNumber: Int, @RequestParam pageSize: Int): ResponseEntity<ResponsePayload<List<CustomerProfileDTO>>> {
        return response(customerService.getAllPaginated(pageNumber, pageSize), HttpStatus.OK)
    }

    // POST ENDPOINTS

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newCustomerProfileDTO: CustomerProfileDTO): ResponseEntity<Any> {
        customerService.updateCurrentOrThrow(newCustomerProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_customer")
    @Transactional
    fun updateCustomer(@RequestBody newCustomerProfileDTO: CustomerProfileDTO): ResponseEntity<Any> {
        customerService.update(newCustomerProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_customer")
    @Transactional
    fun deleteCustomer(@RequestParam userUuid: UUID): ResponseEntity<ResponsePayload<Any?>> {
        return response(customerService.softDelete(userUuid), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping(
        path = ["/upload_profile_image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Transactional
    fun uploadProfileImage(@RequestParam file: MultipartFile): ResponseEntity<ResponsePayload<Any?>> {
        return response(customerService.saveCurrentUserProfileImage(file), HttpStatus.OK)
    }

}