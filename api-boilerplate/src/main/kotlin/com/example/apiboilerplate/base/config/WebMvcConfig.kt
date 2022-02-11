package com.example.apiboilerplate.base.config

import com.example.apiboilerplate.base.interceptors.sys_call_log.ApiCallContextFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Bean
    fun createLoggers(apiCallContextFilter: ApiCallContextFilter): FilterRegistrationBean<ApiCallContextFilter> {
        val registrationBean: FilterRegistrationBean<ApiCallContextFilter> = FilterRegistrationBean<ApiCallContextFilter>()
        registrationBean.filter = apiCallContextFilter
        // Uncomment below to select specific endpoints to be logged to database table SYS_CALL_LOG
        // registrationBean.addUrlPatterns("/sign_up", "/get_user")
        return registrationBean
    }

}
