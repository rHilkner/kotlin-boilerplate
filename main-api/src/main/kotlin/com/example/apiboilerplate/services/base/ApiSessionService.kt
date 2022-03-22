package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.repositories.base.ApiSessionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApiSessionService(
    private val apiSessionRepository: ApiSessionRepository
) {

    companion object { private val log by ApiLogger() }

    fun createNewApiSession(token: String): ApiSession {
        log.debug("Creating new api-session without defined user and permissions and with token [$token]")
        return ApiSession(token, ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress)
    }

    fun createAndSaveApiSession(appUser: AppUser, token: String, permissions: List<Permission> = listOf(), renewExpiration: Boolean = true): ApiSession {
        log.info("Creating and saving to database new session for user [${appUser.email}] with role and permissions [${appUser.role} / $permissions]")
        val newApiSession = ApiSession(appUser, permissions, token, ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress, renewExpiration)
        val apiSession = apiSessionRepository.save(newApiSession)
        ApiSessionContext.getCurrentApiCallContext().apiSession = apiSession
        return apiSession
    }

    fun getActiveApiSession(token: String): ApiSession {

        val apiSession = apiSessionRepository.getApiSessionByTokenAndStatusCd(token, StatusCd.ACTIVE)

        if (apiSession == null) {
            log.error("Invalid authorization token [{}]", token)
            throw ApiExceptionModule.Auth.InvalidSessionToken(token)
        }

        apiSession.lastActivityDt = Date()
        return apiSessionRepository.save(apiSession)

    }

    fun validateApiSession(apiSession: ApiSession, shouldSessionExpire: Boolean, sessionExpiresIn: Long) {
        // If session doesn't expire, then it's valid
        if (!shouldSessionExpire) {
            return
        }

        // Check if session didn't expire yet
        val sessionLastValidDt =
            if (apiSession.renewExpiration) apiSession.lastActivityDt.time else apiSession.startDt.time

        val sessionExpirationDtMillis = sessionLastValidDt + 1000 * sessionExpiresIn
        val now = Date().time
        if (now > sessionExpirationDtMillis) {
            log.debug("Session expired, returning new session")
            throw ApiExceptionModule.Auth.ExpiredSessionToken(apiSession.token)
        }

    }

    fun inactivateCurrentSession() {
        val currentSession = ApiSessionContext.getCurrentApiCallContext().apiSession!!
        log.info("Inactivating session with id [${currentSession.sessionId}]")
        currentSession.statusCd = StatusCd.INACTIVE
        apiSessionRepository.save(currentSession)
        log.debug("Session with id [${currentSession.sessionId}] inactivated")
    }

}