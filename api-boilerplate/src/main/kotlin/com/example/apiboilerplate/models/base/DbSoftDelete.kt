package com.example.apiboilerplate.models.base

import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class DbSoftDelete: DbAuditable() {

    @Column(name = "deleted_status")
    val deletedStatus = false

    @Column(name = "deleted_dt")
    val deletedDt: Date? = null

    @Column(name = "deleted_by")
    val deletedBy: String? = null

}