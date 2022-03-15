package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.enums.Permission
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

    constructor(appUser: AppUser, permissionList: List<Permission> = listOf(), token: String, ipAddress: String, renewExpiration: Boolean) : this(token, ipAddress) {
        this.userId = appUser.userId!!
        this.role = appUser.role
        this.setPermissions(permissionList)
        this.renewExpiration = renewExpiration
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    var sessionId: Long? = null

    /** User associated to permission */
    @Column(name = "user_id")
    var userId: Long? = null

    /** Roles of the user associated to permission (customer, admin) */
    @Convert(converter = UserRole.Converter::class)
    @Column(name = "role")
    lateinit var role: UserRole

    /** Permissions on API - forgot-password feature sends a token by email that have permission to reset password, for example */
    @Column(name = "permissions")
    private var _permissions: String? = null

    val permissions : List<Permission>
        get() {
            if (_permissions == null) {
                return listOf()
            }
            return EnumUtil.stringListToEnumList(_permissions!!.split (",").toList(), Permission::class.java).toList()
        }

    fun setPermissions(permissionList: List<Permission>) {
        val permissionListStr = permissionList.joinToString(",") { p -> p.dbValue }
        this._permissions = permissionListStr.ifEmpty { null }
    }

    fun addPermission(permission: Permission) {
        val newPermissions = permissions.toMutableList()
        newPermissions.add(permission)
        setPermissions(newPermissions)
    }

    /** Access token for the permission */
    @Column(name = "token")
    lateinit var token: String

    /** IP Address used to create permission */
    @Column(name = "ip_address")
    lateinit var ipAddress: String

    /** Status of the permission */
    @Convert(converter = StatusCd.Converter::class)
    @Column(name = "status_cd")
    var statusCd: StatusCd = StatusCd.ACTIVE

    /** When it was activated */
    @Column(name = "start_dt")
    var startDt: Date = Date()

    /** When it was last used */
    @Column(name = "last_activity_dt")
    var lastActivityDt: Date = Date()

    /** In how many seconds should the session expire */
    @Column(name = "expires_in")
    var expiresIn: Long? = null

    /** Should the session expire based in the last-activity (renew = true) or based in the start (renew = false) */
    @Column(name = "renew_expiration")
    var renewExpiration: Boolean = true

}