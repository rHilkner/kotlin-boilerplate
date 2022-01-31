package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository: JpaRepository<AppUser, Long> {

    fun getAppUserByEmail(email: String) : AppUser?

}