package com.example.apiboilerplate.models.address

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@Entity
@Table(name = "master_address", schema = "public", catalog = "extran")
open class MasterAddress: DbAuditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "master_address_id", nullable = false)
    var masterAddressId: String? = null

    @Column(name = "google_place_id", nullable = true)
    var googlePlaceId: String? = null

    @Column(name = "zip", nullable = true)
    var zip: String? = null

    @Column(name = "address_line", nullable = true)
    var addressLine: String? = null

    @Column(name = "address_number", nullable = true)
    var addressNumber: String? = null

    @Column(name = "city", nullable = true)
    var city: String? = null

    @Column(name = "state", nullable = true)
    var state: String? = null

    @Column(name = "country", nullable = true)
    var country: String? = null

    @Column(name = "latitude", nullable = true)
    var latitude: Double? = null

    @Column(name = "longitude", nullable = true)
    var longitude: Double? = null

}

