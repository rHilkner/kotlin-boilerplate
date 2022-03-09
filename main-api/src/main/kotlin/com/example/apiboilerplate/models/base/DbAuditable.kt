package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.repositories.base.AuditableFields
import java.util.*
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditableFields::class)
abstract class DbAuditable {

    @Column(name = "created_dt", updatable = false)
    var createdDt: Date? = null

    @Column(name = "created_by", updatable = false)
    var createdBy: String? = null

    @Column(name = "updated_dt")
    var updatedDt: Date? = null

    @Column(name = "updated_by")
    var updatedBy: String? = null

}