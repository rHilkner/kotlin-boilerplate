package com.example.apiboilerplate.repositories.user

import com.example.apiboilerplate.models.user.PartnerProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartnerProfileRepository: JpaRepository<PartnerProfile, Long> {

    fun findByUserId(userId: Long): PartnerProfile?

}