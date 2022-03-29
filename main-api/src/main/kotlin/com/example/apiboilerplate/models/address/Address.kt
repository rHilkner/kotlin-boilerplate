package com.example.apiboilerplate.models.address

import com.example.apiboilerplate.models.base.DbAuditable
import javax.persistence.*

@Entity
@Table(name = "address", schema = "public", catalog = "extran")
class Address: DbAuditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false)
    var addressId: Long? = null

    @Column(name = "master_address_id", nullable = true)
    var masterAddressId: String? = null

    @OneToOne(targetEntity = MasterAddress::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "master_address_id", insertable = false, updatable = false)
    val masterAddress: MasterAddress? = null

    @Column(name = "address_complement", nullable = true)
    var addressComplement: String? = null

    @Column(name = "address_reference", nullable = true)
    var addressReference: String? = null

}

