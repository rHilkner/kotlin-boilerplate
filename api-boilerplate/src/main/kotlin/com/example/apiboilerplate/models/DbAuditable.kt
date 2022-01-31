package com.example.apiboilerplate.models

import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class DbAuditable {

    @Column(name = "created_dt")
    var createdDt = Date()

    @Column(name = "created_by")
    val createdBy = "KOTLIN_APPLICATION"

    @Column(name = "updated_dt")
    var updatedDt = Date()

    @Column(name = "updated_by")
    val updatedBy: String = "KOTLIN_APPLICATION"

}