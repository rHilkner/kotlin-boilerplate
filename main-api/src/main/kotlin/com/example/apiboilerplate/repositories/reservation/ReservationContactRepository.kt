package com.example.apiboilerplate.repositories.reservation

import com.example.apiboilerplate.models.reservation.ReservationContact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationContactRepository: JpaRepository<ReservationContact, Long> {

}