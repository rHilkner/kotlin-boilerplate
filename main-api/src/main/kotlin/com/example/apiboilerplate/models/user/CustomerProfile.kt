package com.example.apiboilerplate.models.user

import javax.persistence.*


@Entity(name = "CustomerProfile")
@Table(name = "user_customer_profile", schema = "public")
class CustomerProfile(): BaseUserProfileWithImage() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_profile_id")
    var customerProfileId: Long? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "document_id")
    var documentId: String? = null

    constructor(appUser: AppUser) : this() {
        this.userId = appUser.userId!!
    }

}
