package com.example.apiboilerplate.models.base

import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class DbSoftDelete: DbAuditable() {

    @Column(name = "deleted_status")
    var deletedStatus = false

    @Column(name = "deleted_dt")
    var deletedDt: Date? = null

    @Column(name = "deleted_by")
    var deletedBy: String? = null

}