package com.example.apiboilerplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableJpaRepositories
@EnableWebMvc
class ApiBoilerplateApplication {

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<ApiBoilerplateApplication>(*args)
		}
	}

}