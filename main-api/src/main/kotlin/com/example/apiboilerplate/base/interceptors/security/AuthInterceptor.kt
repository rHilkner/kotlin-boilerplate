package com.example.apiboilerplate.base.interceptors.security

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.services.base.AuthService
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor(private var authService: AuthService) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        super.preHandle(request, response, handler)

        val apiSession = authService.authenticate(request)
        ApiSessionContext.getCurrentApiCallContext().apiSession = apiSession
        authService.authorize(ApiSessionContext.getCurrentApiCallContext().request, apiSession, handler)

        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        super.postHandle(request, response, handler, modelAndView)
        ApiSessionContext.clearApiCallContext()
    }

}