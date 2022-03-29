package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository: JpaRepository<Vehicle, Long> {

}