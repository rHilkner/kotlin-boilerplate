package com.example.apiboilerplate.models.user

class Admin(
    appUser: AppUser,
    override val userProfile: AdminProfile
): FullUser(appUser, userProfile)