package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.util.AppUserCreator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@DisplayName("Tests for AppUser Repository")
class AppAdminRepositoryTest {

    @Autowired
    lateinit var appAdminRepository: AppAdminRepository

    val appUserCreator = AppUserCreator()

    @Test
    fun save_CreateAppUser_WhenSuccessful() {
        // Create and save
        val appUser = appUserCreator.createAppUserToBeSaved()
        val appUserSaved = appAdminRepository.save(appUser)
        // Assertions
        Assertions.assertNotNull(appUserSaved)
        Assertions.assertNotNull(appUserSaved.userId)
        Assertions.assertEquals(appUser.email, appUserSaved.email)
        Assertions.assertEquals(appUser.name, appUserSaved.name)
        Assertions.assertEquals(appUser.role, appUserSaved.role)
        Assertions.assertEquals(appUser.password, appUserSaved.password)
    }

    @Test
    fun save_UpdateAppUser_WhenSuccessful() {
        // Create and save
        val appUser = appUserCreator.createAppUserToBeSaved()
        val appUserSaved = appAdminRepository.save(appUser)
        // Update
        appUserSaved.name = "Mary Sousa"
        appUserSaved.email = "mary.sousa@gmail.com"
        val appUserUpdated = appAdminRepository.save(appUserSaved)
        // Assertions
        Assertions.assertEquals(appUserSaved.name, appUserUpdated.name)
        Assertions.assertNotNull(appUserUpdated)
        Assertions.assertNotNull(appUserUpdated.userId)
    }

    @Test
    fun delete_RemoveAppUser_WhenSuccessful() {
        // Create and save
        val appUser = appUserCreator.createAppUserToBeSaved()
        val appUserSaved = appAdminRepository.save(appUser)
        // Delete
        appAdminRepository.delete(appUserSaved)
        val appUserDeleted = appAdminRepository.findByIdOrNull(appUserSaved.userId!!)
        // Assertions
        Assertions.assertNull(appUserDeleted)
    }

    @Test
    fun findByEmail_ReturnsAppUser_WhenSuccessful() {
        // Create and save
        val appUser = appUserCreator.createAppUserToBeSaved()
        appAdminRepository.save(appUser)
        // Find user by email
        val appUserByEmail = appAdminRepository.getAppAdminByEmail(appUser.email)
        // Assertions
        Assertions.assertNotNull(appUserByEmail)
    }

}