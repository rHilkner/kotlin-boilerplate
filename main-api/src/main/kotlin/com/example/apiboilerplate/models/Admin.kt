package com.example.apiboilerplate.models

class Admin(
    appUser: AppUser,
    override val userProfile: AdminProfile
): FullUser(appUser, userProfile)