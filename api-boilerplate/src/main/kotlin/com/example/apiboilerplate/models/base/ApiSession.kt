package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.utils.EnumUtil
import java.util.*
import javax.persistence.*

@Entity(name = "ApiSession")
@Table(name = "api_session", schema = "public")
class ApiSession() {

    constructor(token: String, ipAddress: String) : this() {
        this.token = token
        this.ipAddress = ipAddress
    }

    constructor(appUser: AppUser, token: String, ipAddress: String) : this(token, ipAddress) {
        this.userId = appUser.userId!!
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    var sessionId: Long? = null

    /** User associated to session */
    @Column(name = "user_id")
    var userId: Long? = null

    /** Roles of the user associated to session (equivalent to app_user.roles, which we included here to
     * avoid having to select user from database in every beginning of session) */
    @Convert(converter = UserRole.Converter::class)
    @Column(name = "role")
    lateinit var role: UserRole

    /** Access token for the session */
    @Column(name = "token")
    lateinit var token: String

    /** IP Address used to create session */
    @Column(name = "ip_address")
    lateinit var ipAddress: String

    /** Status of the session */
    @Convert(converter = StatusCd.Converter::class)
    @Column(name = "status_cd")
    var statusCd: StatusCd = StatusCd.ACTIVE

    /** When it was activated */
    @Column(name = "start_dt")
    var startDt: Date = Date()

    /** When it was last used */
    @Column(name = "last_activity_dt")
    var lastActivityDt: Date = Date()

}