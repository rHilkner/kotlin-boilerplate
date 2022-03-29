package com.example.apiboilerplate.models.user

import javax.persistence.*

@Entity(name = "AdminProfile")
@Table(name = "user_admin_profile", schema = "public")
class AdminProfile(): BaseUserProfile() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_profile_id")
    var adminProfileId: Long? = null

    @Column(name = "db_write_permission")
    var dbWritePermission: Boolean = false

    constructor(appUser: AppUser) : this() {
        this.userId = appUser.userId!!
    }

    constructor(appUser: AppUser, dbWritePermission: Boolean) : this(appUser) {
        this.dbWritePermission = dbWritePermission
    }

}