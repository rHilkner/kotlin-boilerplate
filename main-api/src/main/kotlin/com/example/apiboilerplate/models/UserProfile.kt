package com.example.apiboilerplate.models

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class UserProfile: DbAuditable() {

    @Column(name = "user_id")
    var userId: Long = 0

}