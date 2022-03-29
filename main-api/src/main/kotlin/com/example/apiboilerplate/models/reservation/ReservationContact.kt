package com.example.apiboilerplate.models.reservation

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@Entity
@Table(name = "reservation_contact", schema = "public", catalog = "extran")
open class ReservationContact: DbAuditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_contact_id", nullable = false)
    var reservationContactId: Long? = null

    @Column(name = "reservation_id", nullable = false)
    var reservationId: Long? = null

    @Column(name = "contact_order", nullable = false)
    var contactOrder: Int? = null

    @Column(name = "contact_type", nullable = true)
    var contactType: String? = null

    @Column(name = "name", nullable = true)
    var name: String? = null

    @Column(name = "email", nullable = true)
    var email: String? = null

    @Column(name = "phone1", nullable = true)
    var phone1: String? = null

    @Column(name = "phone2", nullable = true)
    var phone2: String? = null

    @Column(name = "address_id", nullable = true)
    var addressId: String? = null

    @Column(name = "document1_type", nullable = true)
    var document1Type: String? = null

    @Column(name = "document1_id", nullable = true)
    var document1Id: String? = null

    @Column(name = "document2_type", nullable = true)
    var document2Type: String? = null

    @Column(name = "document2_id", nullable = true)
    var document2Id: String? = null

}

