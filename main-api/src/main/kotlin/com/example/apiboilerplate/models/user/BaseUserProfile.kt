package com.example.apiboilerplate.models.user

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@MappedSuperclass
abstract class BaseUserProfile: DbAuditable() {

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @OneToOne(targetEntity = AppUser::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    lateinit var appUser: AppUser

}