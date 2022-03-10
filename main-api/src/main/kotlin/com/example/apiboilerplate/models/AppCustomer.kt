package com.example.apiboilerplate.models

import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.utils.ObjectUtil
import javax.persistence.*


@Entity(name = "AppCustomer")
@Table(name = "app_customer", schema = "public")
class AppCustomer: AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    var customerId: Long? = null

    @Column(name = "phone")
    var phone: String? = null

    @Column(name = "document_id")
    var documentId: String? = null

    @Column(name = "address")
    var address: String? = null

    @Column(name = "address_complement")
    var addressComplement: String? = null

    constructor()
    constructor(signUpAppCustomerRequestDTO: SignUpAppCustomerRequestDTO, passwordHash: String) {
        ObjectUtil.copyProps(signUpAppCustomerRequestDTO, this)
        this.passwordHash = passwordHash
    }

}
