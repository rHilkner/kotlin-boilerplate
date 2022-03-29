package com.example.apiboilerplate.models

import com.example.apiboilerplate.models.base.DbSoftDelete
import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "custom_plate", schema = "public", catalog = "extran")
open class CustomPlate: DbSoftDelete() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_plate_id", nullable = false)
    var customPlateId: Long? = null

    @Column(name = "name", nullable = false)
    var name: String? = null

    @Column(name = "price", nullable = true)
    var price: BigDecimal? = null

    @Column(name = "description", nullable = true)
    var description: String? = null

    @Column(name = "photo", nullable = true)
    var photo: String? = null

    @Column(name = "status_cd", nullable = false)
    var statusCd: String? = null

}

