package com.example.apiboilerplate.models.base

import java.util.*
import javax.persistence.*

@Entity(name = "SysErrorLog")
@Table(name = "sys_error_log", schema = "public")
class SysErrorLog : DbAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "error_log_id")
    var errorLogId: Long? = null

    @Column(name = "call_log_id")
    var callLogId: Long? = null

    @Column(name = "http_status")
    var httpStatus: String? = null

    @Column(name = "http_status_code")
    var httpStatusCode: String? = null

    @Column(name = "exception_class")
    lateinit var exceptionClass: String

    @Column(name = "stack_trace")
    lateinit var stackTrace: String

    @Column(name = "error_message")
    lateinit var errorMessage: String

    @Column(name = "debug_message")
    var debugMessage: String? = null

    @Column(name = "exception_timestamp")
    lateinit var exceptionTimestamp: Date

    constructor()
    constructor(callLogId: Long?, httpStatus: String?, httpStatusCode: String, exceptionClass: String, stackTrace: String, errorMessage: String, debugMessage: String?, exceptionTimestamp: Date) {
        this.callLogId = callLogId
        this.httpStatus = httpStatus
        this.httpStatusCode = httpStatusCode
        this.exceptionClass = exceptionClass
        this.stackTrace = stackTrace
        this.errorMessage = errorMessage
        this.debugMessage = debugMessage
        this.exceptionTimestamp = exceptionTimestamp
    }

}