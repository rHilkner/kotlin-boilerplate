package com.example.apiboilerplate.models.user

import com.example.apiboilerplate.dtos.auth.SignUpAdminRequestDTO
import javax.persistence.*

@Entity(name = "AdminProfile")
@Table(name = "admin_profile", schema = "public")
class AdminProfile(): UserProfile() {

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

    constructor(appUser: AppUser, signUpDto: SignUpAdminRequestDTO) : this(appUser)

}