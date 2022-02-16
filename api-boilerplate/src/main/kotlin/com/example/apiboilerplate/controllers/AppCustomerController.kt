package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.AuthAppCustomerResponseDTO
import com.example.apiboilerplate.dtos.auth.CustomerSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.services.AppCustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

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

    @SecuredRole([UserRole.CUSTOMER])
    @GetMapping("/get_current_user")
    fun getCurrentUser(): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        return response(appCustomerService.getCurrentCustomerDto(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.CUSTOMER])
    @PostMapping("/update_current_user")
    fun updateCurrentUser(@RequestBody newAppCustomerDTO: AppCustomerDTO): ResponseEntity<ResponsePayload<AppCustomerDTO>> {
        return response(appCustomerService.updateCurrentCustomer(newAppCustomerDTO), HttpStatus.OK)
    }

}