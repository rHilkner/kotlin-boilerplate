package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.interceptors.security.Secured
import com.example.apiboilerplate.base.logger.LoggerDelegate
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.repositories.ApiSessionRepository
import com.example.apiboilerplate.utils.RandomString
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class AuthService(
    private val passwordEncoder: PasswordEncoder,
    private val apiSessionRepository: ApiSessionRepository
) {

    companion object { private val log by LoggerDelegate() }

    private val AUTHENTICATION_SCHEME = "Bearer"

    @Value("\${authentication.should-session-expire}")
    private val shouldSessionExpire = false

    @Value("\${authentication.session-expires-in}")
    private val sessionExpiresIn = 0L

    @Value("\${authentication.session-token-length}")
    private val sessionTokenLength = 0

    private val randomString by lazy { RandomString(sessionTokenLength) }

    fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun passwordMatchesEncoded(password: String, encoded: String): Boolean {
        return passwordEncoder.matches(password, encoded)
    }

    fun createBlankApiSession(): ApiSession {
        return ApiSession(getNewSessionToken(), ApiCallContext.getCurrentApiCallContext().request.wrapperIpAddress)
    }

    private fun createAndSaveApiSession(appUser: AppUser): ApiSession {
        val newApiSession = ApiSession(appUser, getNewSessionToken(), ApiCallContext.getCurrentApiCallContext().request.wrapperIpAddress)
        val apiSession = apiSessionRepository.save(newApiSession)
        ApiCallContext.getCurrentApiCallContext().apiSession = apiSession
        return apiSession
    }

    fun authenticate(appUser: AppUser, password: String): ApiSession {
        if (!passwordMatchesEncoded(password, appUser.password)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }
        return createAndSaveApiSession(appUser)
    }

    fun authenticate(request: HttpServletRequest): ApiSession {
        log.debug("Attempting to authenticate request: [{}]", request)

        val authToken = getAuthTokenFromRequest(request)
        return getApiSession(authToken)
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
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationHeader == null) {
            log.debug("Missing authorization header")
            return null
        }

        val authorizationHeaderComponents = authorizationHeader.split(" ".toRegex()).toTypedArray()
        if (authorizationHeaderComponents.size != 2 || authorizationHeaderComponents[0] != AUTHENTICATION_SCHEME) {
            log.debug("Missing authorization token")
            return null
        }

        return authorizationHeaderComponents[1]
    }

    fun authorize(request: HttpServletRequest, apiSession: ApiSession, handler: Any) {

        // If it does not have a proper HandlerMethod let it continue and fail for NOT_FOUND.
        if (handler !is HandlerMethod) {
            return
        }

        log.info("Authorizing request: [{}]", request.requestURI)

        val securedRoles = this.getSecuredRoles(handler)

        if (securedRoles.isEmpty()) {
            log.info("Request [{}] is not secured - authorizing request", request.requestURI)
            return
        }

        if (apiSession.statusCd != StatusCd.ACTIVE) {
            log.warn("Attempting to access secured resource [{}] without authenticated session", request.requestURI)
            throw ApiExceptionModule.Auth.UnauthenticatedCallException()
        }

        if (!securedRoles.contains(apiSession.role)) {
            // No role in session in secured group - don't authorize.
            log.warn("No session roles authorized for request: [{}]", request.requestURI)
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(apiSession.role.name, request.method)
        }

        // One of session's roles is in the secured role group - authorize.
        log.debug("Session authorized for request: [{}]", request.requestURI)
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
        var secured: Secured? = null

        // Getting the Secured annotation from the method, if method is annotated.
        if (handlerMethod.method.isAnnotationPresent(Secured::class.java)) {
            secured = handlerMethod.method.getAnnotation(Secured::class.java)
        }

        // If method was not annotated, gets annotation from class.
        if (secured == null && handlerMethod.beanType.isAnnotationPresent(Secured::class.java)) {
            secured = handlerMethod.beanType.getAnnotation(Secured::class.java)
        }

        // Return the list
        return secured?.value?.toList() ?: listOf()
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

}