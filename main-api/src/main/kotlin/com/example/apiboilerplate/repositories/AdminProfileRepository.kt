package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.AdminProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminProfileRepository: JpaRepository<AdminProfile, Long> {

    fun findByUserId(userId: Long): AdminProfile?
    fun deleteByAdminProfileId(adminProfileId: Long)

}