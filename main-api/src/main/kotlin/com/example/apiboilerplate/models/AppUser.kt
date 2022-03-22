package com.example.apiboilerplate.models

import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.base.DbSoftDelete
import java.util.*
import javax.persistence.*

@Entity(name = "AppUser")
@Table(name = "app_user", schema = "public")
class AppUser(): DbSoftDelete() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = null

    @Column(name = "user_uuid")
    lateinit var userUuid: UUID

    @Convert(converter = UserRole.Converter::class)
    @Column(name = "role")
    lateinit var role: UserRole

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "password_hash")
    lateinit var passwordHash: String

    @Convert(converter = StatusCd.Converter::class)
    @Column(name = "status_cd")
    var statusCd: StatusCd = StatusCd.ACTIVE

    @Column(name = "last_access_dt")
    var lastAccessDt: Date = Date()

    @Column(name = "last_access_ip")
    var lastAccessIp: String? = null

    @Column(name = "last_login_dt")
    var lastLoginDt: Date = Date()

    @Column(name = "sign_up_dt")
    var signUpDt: Date = Date()

    constructor(signUpRequestDTO: SignUpRequestDTO, uuid: UUID, role: UserRole, passwordHash: String) : this() {
        this.userUuid = uuid
        this.role = role
        this.email = signUpRequestDTO.email
        this.name = signUpRequestDTO.name
        this.passwordHash = passwordHash
    }

}