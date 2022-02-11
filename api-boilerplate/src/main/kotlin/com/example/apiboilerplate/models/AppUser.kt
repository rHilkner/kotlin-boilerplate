package com.example.apiboilerplate.models

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.base.DbSoftDelete
import javax.persistence.*

@Entity(name = "AppUser")
@Table(name = "app_user", schema = "public")
class AppUser: DbSoftDelete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "password")
    lateinit var password: String

    @Convert(converter = UserRole.Converter::class)
    @Column(name = "role")
    lateinit var role: UserRole

    @Convert(converter = StatusCd.Converter::class)
    @Column(name = "status_cd")
    var statusCd: StatusCd = StatusCd.ACTIVE

    @Column(name = "last_access_ip")
    var lastAccessIp: String? = null

    constructor()
    constructor(email: String, name: String, password: String, role: UserRole) {
        this.email = email
        this.name = name
        this.password = password
        this.role = role
        this.lastAccessIp = ApiCallContext.getCurrentApiCallContext().request.wrapperIpAddress
    }

}