package com.example.apiboilerplate.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig(): WebSecurityConfigurerAdapter() {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun encoder(): PasswordEncoder? {
        return bCryptPasswordEncoder
    }

}