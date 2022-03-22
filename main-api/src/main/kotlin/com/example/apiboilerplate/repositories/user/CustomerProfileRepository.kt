package com.example.apiboilerplate.repositories.user

import com.example.apiboilerplate.models.user.CustomerProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerProfileRepository: JpaRepository<CustomerProfile, Long> {

    fun findByUserId(userId: Long) : CustomerProfile?
    fun deleteByCustomerProfileId(customerProfileId: Long)

}