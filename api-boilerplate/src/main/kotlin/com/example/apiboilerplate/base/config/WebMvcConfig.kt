package com.example.apiboilerplate.base.config

import com.example.apiboilerplate.base.interceptors.security.AuthInterceptor
import com.example.apiboilerplate.base.interceptors.sys_call_log.ApiCallContextFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var authInterceptor: AuthInterceptor

    @Bean
    fun createLoggers(apiCallContextFilter: ApiCallContextFilter): FilterRegistrationBean<ApiCallContextFilter> {
        val registrationBean: FilterRegistrationBean<ApiCallContextFilter> = FilterRegistrationBean<ApiCallContextFilter>()
        registrationBean.filter = apiCallContextFilter
        // Uncomment below to select specific endpoints to be logged to database table SYS_CALL_LOG
        // registrationBean.addUrlPatterns("/sign_up", "/get_user")
        return registrationBean
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(authInterceptor)
    }

}
