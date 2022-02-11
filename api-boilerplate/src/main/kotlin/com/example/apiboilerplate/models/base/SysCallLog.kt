package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.logger.LoggerDelegate
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
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

    constructor(apiCallContext: ApiCallContext) : this() {
        updateData(apiCallContext)
    }

    fun updateData(apiCallContext: ApiCallContext) {

        this.executionId = apiCallContext.executionId

        this.url = apiCallContext.request.wrapperUrl
        this.ip = apiCallContext.request.wrapperIpAddress
        this.method = apiCallContext.request.wrapperMethod
        this.endpoint = apiCallContext.request.wrapperEndpoint
        this.parameters = apiCallContext.request.wrapperParameters
        this.requestHeaders = apiCallContext.request.wrapperHeaders
        this.requestBody =
            apiCallContext.request.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(apiCallContext.request.wrapperEndpoint, it, true) }

        this.httpStatus = apiCallContext.response.wrapperHttpStatus
        this.responseHeaders = apiCallContext.response.wrapperHeaders
        this.responseBody =
            apiCallContext.response.wrapperBody?.let { extractBodyWithSensitiveInfoBlurred(apiCallContext.request.wrapperEndpoint, it, false) }

        this.startDt = apiCallContext.startDt
        this.endDt = apiCallContext.endDt
        this.updatedDt = Date()
    }

    private fun extractBodyWithSensitiveInfoBlurred(endpoint: String, body: String, isRequest: Boolean): String? {

        val objectMapper = ObjectMapper()
        var bodyBlurred = body

        // Blur password field on incoming sign_up requests
        if ("/sign_up".equals(endpoint, ignoreCase = true) && isRequest) {
            val signUpRequestDTO: SignUpRequestDTO = objectMapper.readValue(body, SignUpRequestDTO::class.java)
            signUpRequestDTO.password = "****"
            bodyBlurred = objectMapper.writeValueAsString(signUpRequestDTO)
        }

        return try {
            JsonUtils.simplify(bodyBlurred)
        } catch (ex: Exception) {
            log.debug("Could not simplify JSON from request - probably not a JSON")
            bodyBlurred
        }
    }

}