package com.example.apiboilerplate.models.user

class Customer(
    appUser: AppUser,
    override val userProfile: CustomerProfile
): FullUser(appUser, userProfile)