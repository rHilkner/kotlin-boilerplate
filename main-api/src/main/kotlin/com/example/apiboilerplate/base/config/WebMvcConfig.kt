package com.example.apiboilerplate.base.config

import com.example.apiboilerplate.base.interceptors.security.AuthInterceptor
import com.example.apiboilerplate.base.interceptors.sys_call_log.ApiCallContextFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(var authInterceptor: AuthInterceptor) : WebMvcConfigurer {

    // Endpoints to be logged to database table SYS_CALL_LOG
//    val urlPatternsToBePersisted: Array<String> = arrayOf("/sign_up", "/get_user")
    val urlPatternsToBePersisted: Array<String> = emptyArray()

    @Value("\${boilerplate-env.server.allowed-origins}")
    private lateinit var allowedOrigins: List<String>

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(authInterceptor)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        super.addCorsMappings(registry)
        registry.addMapping("/**").allowedOrigins(*this.allowedOrigins.map { it }.toTypedArray())
    }

    @Bean
    fun createApiCallLoggers(apiCallContextFilter: ApiCallContextFilter): FilterRegistrationBean<ApiCallContextFilter> {
        val registrationBean: FilterRegistrationBean<ApiCallContextFilter> = FilterRegistrationBean<ApiCallContextFilter>()
        registrationBean.filter = apiCallContextFilter
        registrationBean.addUrlPatterns(*this.urlPatternsToBePersisted)
        return registrationBean
    }

}
