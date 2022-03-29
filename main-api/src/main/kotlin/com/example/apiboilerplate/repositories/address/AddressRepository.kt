package com.example.apiboilerplate.repositories.address

import com.example.apiboilerplate.models.address.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository: JpaRepository<Address, Long> {

    fun findByMasterAddressId(masterAddressId: String): Address?

}