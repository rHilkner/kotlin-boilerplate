package com.example.apiboilerplate.models

import javax.persistence.*

@Entity(name = "AppUser")
@Table(name = "app_user", schema = "public")
class AppUser: DbSoftDelete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = null

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "password")
    lateinit var password: String

    constructor()
    constructor(email: String, name: String, password: String) {
        this.email = email
        this.name = name
        this.password = password
    }

}