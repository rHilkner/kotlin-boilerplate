package com.example.apiboilerplate.models

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.base.DbSoftDelete
import java.util.*
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AppUser: DbSoftDelete() {

    val userId: Long?
        get() {
            when (this) {
                is AppCustomer -> {
                    return this.customerId
                }
                is AppAdmin -> {
                    return this.adminId
                }
            }
            throw RuntimeException()
        }

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "profile_image_path")
    var profileImagePath: String? = null

    @Column(name = "password_hash")
    lateinit var passwordHash: String

    val role: UserRole
        get() {
            when (this) {
                is AppCustomer -> {
                    return UserRole.CUSTOMER
                }
                is AppAdmin -> {
                    return UserRole.ADMIN
                }
            }
            throw RuntimeException()
        }


    @Convert(converter = StatusCd.Converter::class)
    @Column(name = "status_cd")
    var statusCd: StatusCd = StatusCd.ACTIVE

    @Column(name = "last_access_dt")
    var lastAccessDt: Date = Date()

    @Column(name = "last_access_ip")
    var lastAccessIp: String? = ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress

}