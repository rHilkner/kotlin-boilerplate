package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.services.AppAdminService
import com.example.apiboilerplate.util.AppUserCreator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class AppAdminControllerTest {

    @InjectMocks
    private lateinit var appAdminController: AppAdminController
    @Mock
    private var appAdminServiceMock: AppAdminService = Mockito.mock(AppAdminService::class.java)

    private val appUserCreator = AppUserCreator()

    @BeforeEach
    fun setup() {
        BDDMockito
            .`when`(appAdminServiceMock.getAdminDtoByEmail(ArgumentMatchers.anyObject()))
            .thenReturn(appUserCreator.createAppUserDto())
    }

    @Test
    fun getUserByEmail_ReturnAppUser_WhenSuccessful() {
        // Getting app user by email
        val appUserExpected = appUserCreator.createAppUserDto()
        val appUserFound = appAdminController.getUserByEmail(appUserExpected.email).body!!.payload
        // Assertions
        Assertions.assertNotNull(appUserFound)
        Assertions.assertNotNull(appUserFound!!.adminId)
        Assertions.assertEquals(appUserExpected.email, appUserFound.email)
        Assertions.assertEquals(appUserExpected.name, appUserFound.name)
        Assertions.assertEquals(appUserExpected.role, appUserFound.role)
    }

}