package com.example.apiboilerplate.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class SecurityConfig {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun encoder(): PasswordEncoder {
        return bCryptPasswordEncoder
    }

}