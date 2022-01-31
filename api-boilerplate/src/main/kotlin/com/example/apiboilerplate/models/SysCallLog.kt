package com.example.apiboilerplate.models

import com.example.apiboilerplate.base.ServiceContext
import com.example.apiboilerplate.base.logger.LoggerDelegate
import com.example.apiboilerplate.dtos.SignUpDTO
import com.example.apiboilerplate.utils.JsonUtils
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*
import javax.persistence.*

@Entity(name = "SysCallLog")
@Table(name = "sys_call_log", schema = "public")
class SysCallLog() : DbAuditable() {

    companion object { private val log by LoggerDelegate() }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_log_id")
    var callLogId: Long? = null

    @Column(name = "execution_id")
    lateinit var executionId: String

    @Column(name = "url")
    var url: String? = null

    @Column(name = "ip")
    var ip: String? = null

    @Column(name = "method")
    var method: String? = null

    @Column(name = "endpoint")
    var endpoint: String? = null

    @Column(name = "parameters")
    var parameters: String? = null

    @Column(name = "request_body")
    var requestBody: String? = null

    @Column(name = "request_headers")
    var requestHeaders: String? = null

    @Column(name = "http_status")
    var httpStatus: Int? = null

    @Column(name = "response_body")
    var responseBody: String? = null

    @Column(name = "response_headers")
    var responseHeaders: String? = null

    @Column(name = "start_dt")
    var startDt: Date? = null

    @Column(name = "end_dt")
    var endDt: Date? = null

    constructor(ctx: ServiceContext) : this() {
        updateData(ctx)
    }

    fun updateData(ctx: ServiceContext) {

        this.executionId = ctx.executionId

        this.url = ctx.request.wrapperUrl
        this.ip = ctx.request.wrapperIp
        this.method = ctx.request.wrapperMethod
        this.endpoint = ctx.request.wrapperEndpoint
        this.parameters = ctx.request.wrapperParameters
        this.requestHeaders = ctx.request.wrapperHeaders
        this.requestBody =
            ctx.request.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(ctx.request.wrapperEndpoint, it, true) }

        this.httpStatus = ctx.response.wrapperHttpStatus
        this.responseHeaders = ctx.response.wrapperHeaders
        this.responseBody =
            ctx.response.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(ctx.request.wrapperEndpoint, it, false) }

        this.startDt = ctx.startDt
        this.endDt = ctx.endDt
        this.updatedDt = Date()
    }

    private fun extractBodyWithSensitiveInfoBlurred(endpoint: String, body: String, isRequest: Boolean): String? {

        val objectMapper = ObjectMapper()
        var bodyBlurred = body

        // Blur password field on incoming sign_up requests
        if ("/sign_up".equals(endpoint, ignoreCase = true) && isRequest) {
            val signUpDTO: SignUpDTO = objectMapper.readValue(body, SignUpDTO::class.java)
            signUpDTO.password = "****"
            bodyBlurred = objectMapper.writeValueAsString(signUpDTO)
        }

        return try {
            JsonUtils.simplify(bodyBlurred)
        } catch (ex: Exception) {
            log.debug("Could not simplify JSON from request - probably not a JSON")
            bodyBlurred
        }
    }

}