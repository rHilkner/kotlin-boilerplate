package com.example.apiboilerplate.models.user

import javax.persistence.*


@Entity(name = "CustomerProfile")
@Table(name = "customer_profile", schema = "public")
class CustomerProfile: UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_profile_id")
    var customerProfileId: Long? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "document_id")
    var documentId: String? = null

    @Column(name = "address")
    var address: String? = null

    @Column(name = "address_complement")
    var addressComplement: String? = null

    @Column(name = "profile_image_path")
    var profileImagePath: String? = null

    constructor()
    constructor(appUser: AppUser) {
        this.userId = appUser.userId!!
    }

}
