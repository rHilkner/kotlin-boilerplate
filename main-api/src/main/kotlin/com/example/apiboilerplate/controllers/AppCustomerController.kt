package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.CustomerDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppUserService
import com.example.apiboilerplate.services.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/customer")
class AppCustomerController(
    private val customerService: CustomerService,
    private val appUserService: AppUserService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<LoginAppCustomerResponseDTO>> {
        return response(customerService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid signUpAppCustomerRequestDTO: SignUpAppCustomerRequestDTO): ResponseEntity<ResponsePayload<LoginAppCustomerResponseDTO>> {
        return response(customerService.signUp(signUpAppCustomerRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forgotPassword(email, UserRole.CUSTOMER), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.resetPassword(resetPasswordRequest), HttpStatus.OK)
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
    fun getCurrentUser(): ResponseEntity<ResponsePayload<CustomerDTO>> {
        return response(customerService.getCurrentCustomerDto(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_customer_by_email")
    fun getCustomerByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<CustomerProfileDTO>> {
        val appUserDto = customerService.getCustomerDtoByEmail(email)

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

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newCustomerDTO: CustomerDTO): ResponseEntity<ResponsePayload<CustomerDTO>> {
        return response(customerService.updateCurrentCustomer(newCustomerDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_customer")
    @Transactional
    fun updateCustomer(@RequestBody newCustomerDTO: CustomerDTO): ResponseEntity<ResponsePayload<CustomerDTO>> {
        return response(customerService.updateCustomer(newCustomerDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_customer")
    @Transactional
    fun deleteCustomer(@RequestParam customerId: Long): ResponseEntity<ResponsePayload<Any?>> {
        return response(customerService.deleteCustomer(customerId), HttpStatus.OK)
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