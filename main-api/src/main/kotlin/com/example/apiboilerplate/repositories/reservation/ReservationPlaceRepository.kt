package com.example.apiboilerplate.repositories.reservation

import com.example.apiboilerplate.models.reservation.ReservationPlace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationPlaceRepository: JpaRepository<ReservationPlace, Long> {

}