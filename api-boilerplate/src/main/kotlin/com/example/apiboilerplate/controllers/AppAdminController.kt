package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.interceptors.security.Secured
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppAdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(maxAge = 3600)

@RestController
@RequestMapping("/admin")
class AppAdminController(
    private val appAdminService: AppAdminService
): AbstractController() {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.login(loginRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    fun signUp(@RequestBody @Valid adminSignUpRequestDTO: AdminSignUpRequestDTO): ResponseEntity<ResponsePayload<AuthAppAdminResponseDTO>> {
        return response(appAdminService.signUp(adminSignUpRequestDTO), HttpStatus.OK)
    }

    @Secured([UserRole.ADMIN])
    @GetMapping("/profile")
    fun getAppUser(): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        return response(appAdminService.getCurrentAdminDto(), HttpStatus.OK)
    }

    @Secured([UserRole.ADMIN])
    @GetMapping("/get_by_email")
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<ResponsePayload<AppAdminDTO>> {
        val appUserDto = appAdminService.getAdminDtoByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

}