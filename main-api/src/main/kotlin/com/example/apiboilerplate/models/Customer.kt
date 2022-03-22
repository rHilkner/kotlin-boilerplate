package com.example.apiboilerplate.models

class Customer(
    appUser: AppUser,
    override val userProfile: CustomerProfile
): FullUser(appUser, userProfile)