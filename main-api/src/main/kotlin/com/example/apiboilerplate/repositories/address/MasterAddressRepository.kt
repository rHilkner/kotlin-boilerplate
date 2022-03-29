package com.example.apiboilerplate.repositories.address

import com.example.apiboilerplate.models.address.MasterAddress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MasterAddressRepository: JpaRepository<MasterAddress, Long>