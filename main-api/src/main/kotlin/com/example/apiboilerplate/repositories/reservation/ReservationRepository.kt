package com.example.apiboilerplate.repositories.reservation

import com.example.apiboilerplate.models.reservation.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository: JpaRepository<Reservation, Long> {

}