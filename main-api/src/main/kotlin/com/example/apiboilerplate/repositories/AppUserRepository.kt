package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository: JpaRepository<AppUser, Long> {

    fun findByUserIdAndDeletedStatusFalse(userId: Long): AppUser?
    fun findByEmailAndRoleAndDeletedStatusFalse(email: String, role: UserRole): AppUser?

}