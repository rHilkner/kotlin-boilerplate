package com.example.apiboilerplate.enums

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppPaths {

    companion object {
        @JvmField
        var ADMIN_PROFILE_IMAGES: String = ""
        @JvmField
        var CUSTOMER_PROFILE_IMAGES: String = ""
    }

    @Value("\$boilerplate-env.paths.admin-profile-images")
    fun setADMIN_PROFILE_IMAGES(path: String) {
        ADMIN_PROFILE_IMAGES = path
    }

    @Value("\$boilerplate-env.paths.customer-profile-images")
    fun setCUSTOMER_PROFILE_IMAGES(path: String) {
        CUSTOMER_PROFILE_IMAGES = path
    }

}