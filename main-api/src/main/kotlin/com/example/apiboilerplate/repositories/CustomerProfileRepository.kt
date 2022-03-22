package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.CustomerProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerProfileRepository: JpaRepository<CustomerProfile, Long> {

    fun findByUserId(userId: Long) : CustomerProfile?
    fun deleteByCustomerProfileId(customerProfileId: Long)

}