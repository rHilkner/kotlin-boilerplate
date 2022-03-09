package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.utils.JsonUtils
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*
import javax.persistence.*

@Entity(name = "SysCallLog")
@Table(name = "sys_call_log", schema = "public")
class SysCallLog() : DbAuditable() {

    companion object { private val log by ApiLogger() }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_log_id")
    var callLogId: Long? = null

    @Column(name = "transaction_id")
    lateinit var transactionId: String

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

    constructor(apiSessionContext: ApiSessionContext) : this() {
        updateData(apiSessionContext)
    }

    fun updateData(apiSessionContext: ApiSessionContext) {

        this.transactionId = apiSessionContext.transactionId

        this.url = apiSessionContext.request.wrapperUrl
        this.ip = apiSessionContext.request.wrapperIpAddress
        this.method = apiSessionContext.request.wrapperMethod
        this.endpoint = apiSessionContext.request.wrapperEndpoint
        this.parameters = apiSessionContext.request.wrapperParameters
        this.requestHeaders = apiSessionContext.request.wrapperHeaders
        this.requestBody =
            apiSessionContext.request.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(apiSessionContext.request.wrapperEndpoint, it, true) }

        this.httpStatus = apiSessionContext.response.wrapperHttpStatus
        this.responseHeaders = apiSessionContext.response.wrapperHeaders
        this.responseBody =
            apiSessionContext.response.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(apiSessionContext.request.wrapperEndpoint, it, false) }

        this.startDt = apiSessionContext.startDt
        this.endDt = apiSessionContext.endDt
        this.updatedDt = Date()
    }

    private fun extractBodyWithSensitiveInfoBlurred(endpoint: String, body: String, isRequest: Boolean): String? {

        val objectMapper = ObjectMapper()
        var bodyBlurred = body

        // Blur password field on incoming sign_up requests
        if ("/sign_up".equals(endpoint, ignoreCase = true) && isRequest) {
            val adminSignUpRequestDTO: AdminSignUpRequestDTO = objectMapper.readValue(body, AdminSignUpRequestDTO::class.java)
            adminSignUpRequestDTO.password = "****"
            bodyBlurred = objectMapper.writeValueAsString(adminSignUpRequestDTO)
        }

        return try {
            JsonUtils.simplify(bodyBlurred)
        } catch (ex: Exception) {
            log.debug("Could not simplify JSON from request - probably not a JSON")
            bodyBlurred
        }
    }

}