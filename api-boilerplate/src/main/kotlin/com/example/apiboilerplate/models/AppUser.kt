package com.example.apiboilerplate.models

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.base.DbSoftDelete
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AppUser: DbSoftDelete {

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

    @Column(name = "password")
    lateinit var password: String

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

    @Column(name = "last_access_ip")
    var lastAccessIp: String? = null

    constructor()
    constructor(email: String, name: String, password: String) {
        this.email = email
        this.name = name
        this.password = password
        this.lastAccessIp = ApiCallContext.getCurrentApiCallContext().request.wrapperIpAddress
    }

}