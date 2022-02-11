package com.example.apiboilerplate.controllers

import com.example.apiboilerplate.services.AppUserService
import com.example.apiboilerplate.util.AppUserCreator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class AppUserControllerTest {

    @InjectMocks
    private lateinit var appUserController: AppUserController
    @Mock
    private var appUserServiceMock: AppUserService = Mockito.mock(AppUserService::class.java)

    private val appUserCreator = AppUserCreator()

    @BeforeEach
    fun setup() {
        BDDMockito
            .`when`(appUserServiceMock.getUserDtoByEmail(ArgumentMatchers.anyObject()))
            .thenReturn(appUserCreator.createAppUserDto())
    }

    @Test
    fun getUserByEmail_ReturnAppUser_WhenSuccessful() {
        // Getting app user by email
        val appUserExpected = appUserCreator.createAppUserDto()
        val appUserFound = appUserController.getUserByEmail(appUserExpected.email).body!!.payload
        // Assertions
        Assertions.assertNotNull(appUserFound)
        Assertions.assertNotNull(appUserFound!!.userId)
        Assertions.assertEquals(appUserExpected.email, appUserFound.email)
        Assertions.assertEquals(appUserExpected.name, appUserFound.name)
        Assertions.assertEquals(appUserExpected.role, appUserFound.role)
    }

}