package com.example.apiboilerplate.base.interceptors.security

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.services.base.AuthService
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthInterceptor(private var authService: AuthService) : HandlerInterceptor {

    companion object { private val log by ApiLogger() }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        super.preHandle(request, response, handler)

        val apiSession = authService.authenticate(request)
        ApiCallContext.getCurrentApiCallContext().apiSession = apiSession
        authService.authorize(request, apiSession, handler)

        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        super.postHandle(request, response, handler, modelAndView)
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        super.afterCompletion(request, response, handler, ex)
    }

}