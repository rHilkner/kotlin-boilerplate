package com.example.apiboilerplate.models.user

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@MappedSuperclass
abstract class UserProfile: DbAuditable() {

    @Column(name = "user_id")
    var userId: Long = 0

    @OneToOne(targetEntity = AppUser::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val appUser: AppUser? = null

}