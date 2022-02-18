package com.example.apiboilerplate.enums

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppPaths {

    companion object {
        private var ADMIN_PROFILE_IMAGES_DIRECTORY: String = ""
        private var CUSTOMER_PROFILE_IMAGES_DIRECTORY: String = ""

        fun getProfileImageDirectory(appUserId: Long, userRole: UserRole): String {
            return when (userRole) {
                UserRole.ADMIN -> ADMIN_PROFILE_IMAGES_DIRECTORY.replace("{adminId}", appUserId.toString())
                UserRole.CUSTOMER -> CUSTOMER_PROFILE_IMAGES_DIRECTORY.replace("{customerId}", appUserId.toString())
            }
        }

    }

    @Value("\$boilerplate-env.paths.admin-profile-images")
    fun setADMIN_PROFILE_IMAGES(path: String) {
        ADMIN_PROFILE_IMAGES_DIRECTORY = path
    }

    @Value("\$boilerplate-env.paths.public")
    fun setCUSTOMER_PROFILE_IMAGES(path: String) {
        CUSTOMER_PROFILE_IMAGES_DIRECTORY = path
    }

}