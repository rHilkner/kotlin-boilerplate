package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.annotations.SecuredPermission
import com.example.apiboilerplate.base.annotations.SecuredRole
import com.example.apiboilerplate.base.interceptors.sys_call_log.AppHttpRequestWrapper
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.repositories.ApiSessionRepository
import com.example.apiboilerplate.services.AppUserService
import com.example.apiboilerplate.utils.RandomString
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@Service
class AuthService(
    private val passwordEncoder: PasswordEncoder,
    private val apiSessionRepository: ApiSessionRepository,
    private val appUserService: AppUserService
) {

    companion object { private val log by ApiLogger() }

    private val AUTHENTICATION_SCHEME = "Bearer"

    @Value("\${boilerplate-env.authentication.should-session-expire}")
    private val shouldSessionExpire = true

    @Value("\${boilerplate-env.authentication.session-expires-in}")
    private val sessionExpiresIn = 0L

    @Value("\${boilerplate-env.authentication.session-token-length}")
    private val sessionTokenLength = 0

    private val randomString by lazy { RandomString(sessionTokenLength) }

    fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun passwordMatchesEncoded(password: String, encoded: String): Boolean {
        return passwordEncoder.matches(password, encoded)
    }

    fun authenticate(appUser: AppUser, password: String): ApiSession {
        log.info("Attempting to authenticate [${appUser.role} / ${appUser.email}] with password")
        if (!passwordMatchesEncoded(password, appUser.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }
        return createAndSaveApiSession(appUser)
    }

    fun createAndSaveApiSession(appUser: AppUser, permissions: List<Permission> = listOf(), renewExpiration: Boolean = true): ApiSession {
        log.info("Creating new session for user [${appUser.email}] with role and permissions [${appUser.role} / $permissions]")
        val newApiSession = ApiSession(appUser, permissions, getNewSessionToken(), ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress, renewExpiration)
        val apiSession = apiSessionRepository.save(newApiSession)
        ApiSessionContext.getCurrentApiCallContext().apiSession = apiSession
        return apiSession
    }

    fun authenticate(request: HttpServletRequest): ApiSession {
        log.debug("Attempting to authenticate request: [{}]", request)
        val authToken = getAuthTokenFromRequest(request)
        return getApiSession(authToken)
    }

    fun createBlankApiSession(): ApiSession {
        return ApiSession(getNewSessionToken(), ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress)
    }

    private fun getApiSession(token: String?): ApiSession {

        if (token == null || token.isEmpty()) {
            return createBlankApiSession()
        }

        val apiSession = apiSessionRepository.getApiSessionByTokenAndStatusCd(token, StatusCd.ACTIVE)

        if (apiSession == null) {
            log.debug("Invalid authorization token [{}]", token)
            return createBlankApiSession()
        }

        if (shouldSessionExpire) {
            val sessionExpirationDtMillis = apiSession.startDt.time + 1000*sessionExpiresIn
            val now = Date().time
            if (now > sessionExpirationDtMillis) {
                log.debug("Session expired, returning new session")
                return createBlankApiSession()
            }
        }

        apiSession.lastActivityDt = Date()

        return apiSessionRepository.save(apiSession)

    }

    fun getAuthTokenFromRequest(request: HttpServletRequest): String? {
        // Get the Authorization header from the request
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null

        val authorizationHeaderComponents = authorizationHeader.split(" ".toRegex()).toTypedArray()
        if (authorizationHeaderComponents.size != 2 || authorizationHeaderComponents[0] != AUTHENTICATION_SCHEME) {
            log.debug("Authorization token incorrect format")
            return null
        }

        return authorizationHeaderComponents[1]
    }

    fun authorize(request: AppHttpRequestWrapper, apiSession: ApiSession, handler: Any) {

        // If it does not have a proper HandlerMethod let it continue and fail for NOT_FOUND.
        if (handler !is HandlerMethod) {
            return
        }

        log.info("Authorizing request for endpoint [${request.wrapperEndpoint}]")

        val securedRoles = this.getSecuredRoles(handler)
        val securedPermissions = this.getSecuredPermissions(handler)

        if (securedRoles.isEmpty()) {
            log.info("Endpoint [${request.wrapperEndpoint}] is not secured - authorizing request")
            return
        }

        if (apiSession.statusCd != StatusCd.ACTIVE) {
            log.error("Attempting to access secured endpoint [${request.wrapperEndpoint}] without authenticated session")
            throw ApiExceptionModule.Auth.UnauthenticatedCallException()
        }

        if (!securedRoles.contains(apiSession.role)) {
            // No role in session in secured group - don't authorize.
            log.error("No session roles authorized for endpoint [${request.wrapperEndpoint}]")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(apiSession.role, request.wrapperEndpoint)
        }

        // If there are permissions needed and session does not have permission needed, don't authorize
        if (securedPermissions.isNotEmpty() && !securedPermissions.any { p -> apiSession.permissions.contains(p) }) {
            // No role in session in secured group - don't authorize.
            log.error("No session permissions authorized for endpoint [${request.wrapperEndpoint}]")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(apiSession.permissions, request.wrapperEndpoint)

        }

        // One of session's roles is in the secured role group - authorize.
        log.info("Session authorized for endpoint [${request.wrapperEndpoint}]")
        return
    }

    /**
     * Returns secured roles for a given request handlerMethod.
     *
     * Looks first for the annotation on the HanlderMethod method. If not annotatted,
     * looks for the annotation on the class.
     *
     * @param handlerMethod the request HandlerMethod
     * @return list of secured roles for a HandlerMethod, or null if neither the method or the class are annotated.
     */
    private fun getSecuredRoles(handlerMethod: HandlerMethod): List<UserRole> {
        var securedRole: SecuredRole? = null

        // Getting the Secured annotation from the method, if method is annotated.
        if (handlerMethod.method.isAnnotationPresent(SecuredRole::class.java)) {
            securedRole = handlerMethod.method.getAnnotation(SecuredRole::class.java)
        }

        // If method was not annotated, gets annotation from class.
        if (securedRole == null && handlerMethod.beanType.isAnnotationPresent(SecuredRole::class.java)) {
            securedRole = handlerMethod.beanType.getAnnotation(SecuredRole::class.java)
        }

        // Return the list
        return securedRole?.value?.toList() ?: listOf()
    }

    /**
     * Returns secured roles for a given request handlerMethod.
     *
     * Looks first for the annotation on the HandlerMethod method. If not annotated,
     * looks for the annotation on the class.
     *
     * @param handlerMethod the request HandlerMethod
     * @return list of secured roles for a HandlerMethod, or null if neither the method or the class are annotated.
     */
    private fun getSecuredPermissions(handlerMethod: HandlerMethod): List<Permission> {
        var securedPermission: SecuredPermission? = null

        // Getting the Secured annotation from the method, if method is annotated.
        if (handlerMethod.method.isAnnotationPresent(SecuredPermission::class.java)) {
            securedPermission = handlerMethod.method.getAnnotation(SecuredPermission::class.java)
        }

        // If method was not annotated, gets annotation from class.
        if (securedPermission == null && handlerMethod.beanType.isAnnotationPresent(SecuredPermission::class.java)) {
            securedPermission = handlerMethod.beanType.getAnnotation(SecuredPermission::class.java)
        }

        // Return the list
        return securedPermission?.value?.toList() ?: listOf()
    }

    /**
     * Creates a new random token to be used as UserSession token.
     * https://automationrhapsody.com/implement-secure-rest-api-authentication-http/
     *
     * @return a random session token.
     */
    fun getNewSessionToken(): String {
        return this.randomString.nextString()
    }

    fun inactivateCurrentSession() {
        val currentSession = ApiSessionContext.getCurrentApiCallContext().apiSession!!
        log.info("Inactivating session with id [${currentSession.sessionId}]")
        currentSession.statusCd = StatusCd.INACTIVE
        apiSessionRepository.save(currentSession)
        log.debug("Session with id [${currentSession.sessionId}] inactivated")
    }

}