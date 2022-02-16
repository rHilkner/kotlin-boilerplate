package com.example.apiboilerplate.repositories.base

import com.example.apiboilerplate.models.base.DbAuditable
import java.util.*
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

class AuditableFields {

    private val API_USER = "KOTLIN_APPLICATION"

    @PreUpdate
    fun updateAuditableFields(obj: DbAuditable) {
        obj.updatedDt = Date()
        obj.updatedBy = API_USER
    }

    @PrePersist
    fun createAuditableFields(obj: DbAuditable) {
        val now = Date()
        obj.createdDt = now
        obj.updatedDt = now
        obj.createdBy = API_USER
        obj.updatedBy = API_USER
    }

}