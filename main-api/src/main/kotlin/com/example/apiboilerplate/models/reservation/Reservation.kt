package com.example.apiboilerplate.models.reservation

import com.example.apiboilerplate.models.base.DbSoftDelete
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "reservation", schema = "public", catalog = "extran")
open class Reservation: DbSoftDelete() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    var reservationId: Long? = null

    @Column(name = "reservation_uuid", nullable = true)
    var reservationUuid: UUID? = null

    @Column(name = "partner_id", nullable = true)
    var partnerId: Long? = null

    @Column(name = "motorist_id", nullable = true)
    var motoristId: Long? = null

    @Column(name = "vehicle_id", nullable = true)
    var vehicleId: Long? = null

    @Column(name = "start_date", nullable = true)
    var startDate: java.sql.Timestamp? = null

    @Column(name = "end_date", nullable = true)
    var endDate: java.sql.Timestamp? = null

    @Column(name = "other_contacts", nullable = true)
    var otherContacts: String? = null

    @Column(name = "custom_plate", nullable = false)
    var customPlate: Boolean? = null

    @Column(name = "custom_plate_id", nullable = true)
    var customPlateId: Long? = null

    @Column(name = "custom_plate_text", nullable = true)
    var customPlateText: String? = null

    @Column(name = "race_price", nullable = true)
    var racePrice: BigDecimal? = null

    @Column(name = "plate_price", nullable = true)
    var platePrice: BigDecimal? = null

    @Column(name = "reservation_completed", nullable = false)
    var reservationCompleted: Boolean? = null

    @Column(name = "partner_owned_service", nullable = false)
    var partnerOwnedService: Boolean? = null

    @Column(name = "partner_confirmed", nullable = false)
    var partnerConfirmed: Boolean? = null

    @Column(name = "status_payment", nullable = false)
    var statusPayment: String? = null

    @Column(name = "status_cd", nullable = false)
    var statusCd: String? = null

}

