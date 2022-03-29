package com.example.apiboilerplate.models

import com.example.apiboilerplate.models.base.DbSoftDelete
import javax.persistence.*

@Entity
@Table(name = "motorist", schema = "public", catalog = "extran")
open class Motorist: DbSoftDelete() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "motorist_id", nullable = false)
    var motoristId: Long? = null

    @Column(name = "partner_id", nullable = true)
    var partnerId: Long? = null

    @Column(name = "name", nullable = false)
    var name: String? = null

    @Column(name = "email", nullable = false)
    var email: String? = null

    @Column(name = "phone", nullable = false)
    var phone: String? = null

    @Column(name = "cnh", nullable = true)
    var cnh: String? = null

    @Column(name = "pendencies", nullable = true)
    var pendencies: String? = null

    @Column(name = "status_approved", nullable = false)
    var statusApproved: String? = null

    @Column(name = "status_cd", nullable = false)
    var statusCd: String? = null

}

