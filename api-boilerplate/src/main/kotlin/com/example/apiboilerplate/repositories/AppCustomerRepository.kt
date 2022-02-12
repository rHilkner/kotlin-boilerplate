package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.AppCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppCustomerRepository: JpaRepository<AppCustomer, Long> {

    fun getAppCustomerByEmail(email: String) : AppCustomer?

}