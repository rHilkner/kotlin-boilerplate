package com.example.apiboilerplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiBoilerplateApplication {

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<ApiBoilerplateApplication>(*args)
		}
	}

}