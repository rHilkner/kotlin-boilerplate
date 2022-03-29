package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.Motorist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MotoristRepository: JpaRepository<Motorist, Long> {

}