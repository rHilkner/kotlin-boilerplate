package com.example.apiboilerplate.repositories.user

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.user.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppUserRepository: JpaRepository<AppUser, Long> {

    fun findByUserUuid(userUUID: UUID): AppUser?
    fun findByUserIdAndDeletedStatusFalse(userId: Long): AppUser?
    fun findByEmailAndRoleAndDeletedStatusFalse(email: String, role: UserRole): AppUser?

}