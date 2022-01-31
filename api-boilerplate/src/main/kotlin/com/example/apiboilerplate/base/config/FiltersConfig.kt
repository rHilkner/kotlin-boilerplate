package com.example.apiboilerplate.base.config

import com.example.apiboilerplate.base.filters.request_context.ServiceContextFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class FiltersConfig : WebMvcConfigurer {

    @Bean
    fun createLoggers(apiRequestFilter: ServiceContextFilter): FilterRegistrationBean<ServiceContextFilter> {
        val registrationBean: FilterRegistrationBean<ServiceContextFilter> = FilterRegistrationBean<ServiceContextFilter>()
        registrationBean.filter = apiRequestFilter
        // Uncomment below to select specific endpoints to be logged to database table SYS_CALL_LOG
        // registrationBean.addUrlPatterns("/sign_up", "/get_user")
        return registrationBean
    }

}
