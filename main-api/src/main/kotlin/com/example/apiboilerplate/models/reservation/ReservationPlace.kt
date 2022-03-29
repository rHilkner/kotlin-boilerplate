package com.example.apiboilerplate.models.reservation

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@Entity
@Table(name = "reservation_place", schema = "public", catalog = "extran")
open class ReservationPlace: DbAuditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_place_id", nullable = false)
    var reservationPlaceId: Long? = null

    @Column(name = "reservation_id", nullable = false)
    var reservationId: Long? = null

    @Column(name = "address_id", nullable = false)
    var addressId: Long? = null

    @Column(name = "place_name", nullable = true)
    var placeName: String? = null

    @Column(name = "place_phone", nullable = true)
    var placePhone: String? = null

    @Column(name = "place_order", nullable = false)
    var placeOrder: Int? = null

    @Column(name = "wait_time", nullable = false)
    var waitTime: Int? = null

    @Column(name = "race_time_from_last_place", nullable = true)
    var raceTimeFromLastPlace: java.math.BigInteger? = null

}

