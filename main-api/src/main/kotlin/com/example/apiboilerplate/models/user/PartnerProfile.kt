package com.example.apiboilerplate.models.user

import com.example.apiboilerplate.models.Motorist
import com.example.apiboilerplate.models.address.Address
import javax.persistence.*

@Entity
@Table(name = "user_partner_profile", schema = "public", catalog = "extran")
open class PartnerProfile(): BaseUserProfileWithImage() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_profile_id", nullable = false)
    var partnerProfileId: Long? = null

    @Column(name = "main_motorist_id", nullable = true)
    var mainMotoristId: Long? = null

    @OneToOne(targetEntity = Motorist::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "main_motorist_id", insertable = false, updatable = false)
    val motorist: Motorist? = null

    @Column(name = "address_id", nullable = true)
    var addressId: Long? = null

    @OneToOne(targetEntity = Address::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    val address: Address? = null

    @Column(name = "payment_email", nullable = true)
    var paymentEmail: String? = null

    @Column(name = "phone_commercial", nullable = true)
    var phoneCommercial: String? = null

    @Column(name = "phone_personal", nullable = true)
    var phonePersonal: String? = null

    @Column(name = "phone_residential", nullable = true)
    var phoneResidential: String? = null

    @Column(name = "rg", nullable = true)
    var rg: String? = null

    @Column(name = "cpf_cnpj", nullable = true)
    var cpfCnpj: String? = null

    constructor(appUser: AppUser) : this() {
        this.userId = appUser.userId!!
    }

}

