package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.interceptors.security.Secured
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(maxAge = 3600)

@RestController
class AppUserController(
    private val appUserService: AppUserService
): AbstractController() {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<AuthResponseDTO>> {
        return response(appUserService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    fun signUp(@RequestBody @Valid signUpRequestDTO: SignUpRequestDTO): ResponseEntity<ResponsePayload<AuthResponseDTO>> {
        return response(appUserService.signUp(signUpRequestDTO), HttpStatus.OK)
    }

    @Secured([UserRole.CUSTOMER])
    @GetMapping("/user_profile")
    fun getAppUser(): ResponseEntity<ResponsePayload<AppUserDTO?>> {
        return response(appUserService.getCurrentUserDto(), HttpStatus.OK)
    }

    @Secured([UserRole.ADMIN])
    @GetMapping("/get_user_by_email")
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AppUserDTO>> {
        val appUserDto = appUserService.getUserDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

}