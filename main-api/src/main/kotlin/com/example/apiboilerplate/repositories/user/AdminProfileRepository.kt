package com.example.apiboilerplate.repositories.user

import com.example.apiboilerplate.models.user.AdminProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminProfileRepository: JpaRepository<AdminProfile, Long> {

    fun findByUserId(userId: Long): AdminProfile?

}