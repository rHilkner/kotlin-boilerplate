package com.example.apiboilerplate.models

import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.utils.ObjectUtil
import javax.persistence.*

@Entity(name = "AppAdmin")
@Table(name = "app_admin", schema = "public")
class AppAdmin: AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    var adminId: Long? = null

    constructor()
    constructor(adminSignUpRequestDTO: AdminSignUpRequestDTO) {
        ObjectUtil.copyProps(this, adminSignUpRequestDTO)
    }

}