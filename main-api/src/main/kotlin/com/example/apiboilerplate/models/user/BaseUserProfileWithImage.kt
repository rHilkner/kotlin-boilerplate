package com.example.apiboilerplate.models.user

import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseUserProfileWithImage: BaseUserProfile() {

    @Column(name = "profile_image_path", nullable = true)
    var profileImagePath: String? = null

}