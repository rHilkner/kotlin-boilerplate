package com.example.apiboilerplate.models

import com.example.apiboilerplate.models.base.DbSoftDelete
import javax.persistence.*

@Entity
@Table(name = "vehicle", schema = "public", catalog = "extran")
open class Vehicle: DbSoftDelete() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id", nullable = false)
    var vehicleId: Long? = null

    @Column(name = "partner_id", nullable = false)
    var partnerId: Long? = null

    @Column(name = "preferred_motorist_id", nullable = true)
    var preferredMotoristId: Long? = null

    @Column(name = "name", nullable = true)
    var name: String? = null

    @Column(name = "brand", nullable = true)
    var brand: String? = null

    @Column(name = "model", nullable = true)
    var model: String? = null

    @Column(name = "year", nullable = true)
    var year: Int? = null

    @Column(name = "max_passengers", nullable = true)
    var maxPassengers: Int? = null

    @Column(name = "color", nullable = true)
    var color: String? = null

    @Column(name = "door_count", nullable = true)
    var doorCount: String? = null

    @Column(name = "convertible", nullable = true)
    var convertible: Boolean? = null

    @Column(name = "limousine", nullable = true)
    var limousine: Boolean? = null

    @Column(name = "speed_factor", nullable = true)
    var speedFactor: Double? = null

    @Column(name = "address_id", nullable = true)
    var addressId: Long? = null

    @Column(name = "value_start", nullable = true)
    var valueStart: Double? = null

    @Column(name = "value_km_driven", nullable = true)
    var valueKmDriven: Double? = null

    @Column(name = "value_minute_stopped", nullable = true)
    var valueMinuteStopped: Double? = null

    @Column(name = "value_minute_driving", nullable = true)
    var valueMinuteDriving: Double? = null

    @Column(name = "pendencies", nullable = true)
    var pendencies: String? = null

    @Column(name = "status_approved", nullable = false)
    var statusApproved: String? = null

    @Column(name = "status_cd", nullable = false)
    var statusCd: String? = null

    @Column(name = "photo1", nullable = true)
    var photo1: String? = null

    @Column(name = "photo2", nullable = true)
    var photo2: String? = null

    @Column(name = "photo3", nullable = true)
    var photo3: String? = null

    @Column(name = "photo4", nullable = true)
    var photo4: String? = null

    @Column(name = "photo5", nullable = true)
    var photo5: String? = null

    @Column(name = "photo6", nullable = true)
    var photo6: String? = null

    @Column(name = "photo7", nullable = true)
    var photo7: String? = null

    @Column(name = "photo8", nullable = true)
    var photo8: String? = null

    @Column(name = "photo9", nullable = true)
    var photo9: String? = null

    @Column(name = "crlv_photo", nullable = true)
    var crlvPhoto: String? = null

}

