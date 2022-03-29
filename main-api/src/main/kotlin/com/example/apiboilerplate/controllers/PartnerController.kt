package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.PartnerProfileDTO
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.services.user.AppUserService
import com.example.apiboilerplate.services.user.PartnerProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/partner")
class PartnerController(
    private val partnerService: PartnerProfileService,
    private val appUserService: AppUserService
): AbstractController() {

    // AUTH ENDPOINTS

    @PostMapping("/login")
    @Transactional
    fun login(@RequestBody @Valid loginRequestDTO: LoginRequestDTO): ResponseEntity<ResponsePayload<LoginPartnerResponseDTO>> {
        return response(partnerService.login(loginRequestDTO, UserRole.PARTNER), HttpStatus.OK)
    }

    @PostMapping("/sign_up")
    @Transactional
    fun signUp(@RequestBody @Valid signUpRequestDTO: SignUpRequestDTO): ResponseEntity<ResponsePayload<LoginPartnerResponseDTO>> {
        return response(partnerService.signUp(signUpRequestDTO, UserRole.PARTNER), HttpStatus.OK)
    }

    @PostMapping("/forgot_password")
    @Transactional
    fun forgotPassword(@RequestParam email: String): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forgotPassword(email, UserRole.PARTNER), HttpStatus.OK)
    }

    @SecuredRole([UserRole.PARTNER])
    @PostMapping("/reset_password")
    @Transactional
    fun resetPassword(@RequestBody resetPasswordRequestDTO: ResetPasswordRequestDTO): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.resetPassword(resetPasswordRequestDTO), HttpStatus.OK)
    }

    @SecuredRole([UserRole.PARTNER])
    @SecuredPermission([Permission.RESET_PASSWORD])
    @PostMapping("/force_reset_password")
    @Transactional
    fun forceResetPassword(@RequestBody req: ForceResetPasswordRequest): ResponseEntity<ResponsePayload<Any?>> {
        return response(appUserService.forceResetPassword(req.newPassword), HttpStatus.OK)
    }

    // GET ENDPOINTS

    @SecuredRole([UserRole.PARTNER])
    @GetMapping("/get_current_user")
    fun getCurrentUser(): ResponseEntity<ResponsePayload<PartnerProfileDTO>> {
        return response(partnerService.getCurrentDtoOrThrow(), HttpStatus.OK)
    }

    @SecuredRole([UserRole.PARTNER])
    @GetMapping("/download_current_user_profile_image")
    fun getCurrentUserProfileImage(): ByteArray? {
        return partnerService.downloadCurrentUserProfileImage()
    }

    @SecuredRole([UserRole.ADMIN])
    @GetMapping("/get_all_users")
    fun getAllUsers(@RequestParam pageNumber: Int, @RequestParam pageSize: Int): ResponseEntity<ResponsePayload<List<PartnerProfileDTO>>> {
        return response(partnerService.getAllPaginated(pageNumber, pageSize), HttpStatus.OK)
    }

    // POST ENDPOINTS

    @SecuredRole([UserRole.PARTNER])
    @PostMapping("/update_current_user")
    @Transactional
    fun updateCurrentUser(@RequestBody newPartnerProfileDTO: PartnerProfileDTO): ResponseEntity<Any> {
        partnerService.updateCurrentOrThrow(newPartnerProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/update_partner")
    @Transactional
    fun updatePartner(@RequestBody newPartnerProfileDTO: PartnerProfileDTO): ResponseEntity<Any> {
        partnerService.update(newPartnerProfileDTO)
        return response(HttpStatus.OK)
    }

    @SecuredRole([UserRole.ADMIN])
    @PostMapping("/delete_partner")
    @Transactional
    fun deletePartner(@RequestParam userUuid: UUID): ResponseEntity<ResponsePayload<Any?>> {
        return response(partnerService.softDelete(userUuid), HttpStatus.OK)
    }

    @SecuredRole([UserRole.PARTNER])
    @PostMapping(
        path = ["/upload_profile_image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Transactional
    fun uploadProfileImage(@RequestParam file: MultipartFile): ResponseEntity<ResponsePayload<Any?>> {
        return response(partnerService.saveCurrentUserProfileImage(file), HttpStatus.OK)
    }

}