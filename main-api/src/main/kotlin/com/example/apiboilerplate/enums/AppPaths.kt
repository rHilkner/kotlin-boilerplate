package com.example.apiboilerplate.enums

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppPaths {

    companion object {
        private lateinit var USERS_PROFILE_IMAGE_DIRECTORY: String
        private lateinit var USERS_VEHICLE_CRLV_DIRECTORY: String
        private lateinit var USERS_VEHICLE_IMAGES_DIRECTORY: String
        private lateinit var APP_CUSTOM_PLATES_DIRECTORY: String

        fun getProfileImageDirectory(appUserId: Long): String {
            return USERS_PROFILE_IMAGE_DIRECTORY.replace("{userId}", appUserId.toString())
        }

        fun getVehycleCrlvDirectory(appUserId: Long): String {
            return USERS_VEHICLE_CRLV_DIRECTORY.replace("{userId}", appUserId.toString())
        }

        fun getVehycleImagesDirectory(appUserId: Long): String {
            return USERS_VEHICLE_IMAGES_DIRECTORY.replace("{userId}", appUserId.toString())
        }

        fun getCustomPlatesDirectory(appUserId: Long): String {
            return APP_CUSTOM_PLATES_DIRECTORY.replace("{userId}", appUserId.toString())
        }

    }

    @Value("\$boilerplate-env.paths.private.profile-image")
    fun setUSERS_PROFILE_IMAGE_DIRECTORY(path: String) {
        USERS_PROFILE_IMAGE_DIRECTORY = path
    }

    @Value("\$boilerplate-env.paths.private.crlv-image")
    fun setUSERS_VEHICLE_CRLV_DIRECTORY(path: String) {
        USERS_VEHICLE_CRLV_DIRECTORY = path
    }

    @Value("\$boilerplate-env.paths.public.vehicle-images")
    fun setUSERS_VEHICLE_IMAGES_DIRECTORY(path: String) {
        USERS_VEHICLE_IMAGES_DIRECTORY = path
    }

    @Value("\$boilerplate-env.paths.public.custom-plate-images")
    fun setAPP_CUSTOM_PLATES_DIRECTORY(path: String) {
        APP_CUSTOM_PLATES_DIRECTORY = path
    }

}