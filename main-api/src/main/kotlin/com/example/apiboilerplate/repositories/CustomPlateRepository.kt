package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.CustomPlate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomPlateRepository: JpaRepository<CustomPlate, Long>