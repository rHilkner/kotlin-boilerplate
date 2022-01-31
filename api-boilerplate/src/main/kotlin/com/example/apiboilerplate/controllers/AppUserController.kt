package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.dtos.SignUpDTO
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

    @PostMapping("/sign_up")
    fun signUp(@RequestBody @Valid signUpDTO: SignUpDTO): ResponseEntity<Any> {
        appUserService.signUpUser(signUpDTO)
        return response("Success!", HttpStatus.OK)
    }

    @GetMapping("/get_user_by_email")
    @ResponseBody
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<Any> {
        val appUserDto = appUserService.getUserByEmail(email)

        if (appUserDto != null) {
            return response(appUserDto, HttpStatus.OK)
        } else {
            throw ApiExceptionModule.User.UserNotFoundException(email)
        }
    }

}