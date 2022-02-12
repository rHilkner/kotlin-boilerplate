package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.interceptors.security.Secured
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppCustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(maxAge = 3600)

@RestController
@RequestMapping("/customer")
class AppCustomerController(
    private val appCustomerService: AppCustomerService
): AbstractController() {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<AuthAppCustomerResponseDTO>> {
        return response(appCustomerService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    fun signUp(@RequestBody @Valid customerSignUpRequestDTO: CustomerSignUpRequestDTO): ResponseEntity<ResponsePayload<AuthAppCustomerResponseDTO>> {
        return response(appCustomerService.signUp(customerSignUpRequestDTO), HttpStatus.OK)
    }

    @Secured([UserRole.CUSTOMER])
    @GetMapping("/profile")
    fun getAppUser(): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        return response(appCustomerService.getCurrentCustomerDto(), HttpStatus.OK)
    }

    @Secured([UserRole.CUSTOMER])
    @GetMapping("/get_by_email")
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        val appUserDto = appCustomerService.getCustomerDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

}