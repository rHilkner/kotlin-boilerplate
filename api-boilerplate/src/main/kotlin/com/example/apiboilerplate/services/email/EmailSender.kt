package com.example.apiboilerplate.services.email

interface EmailSender {
    fun send(to: String, text: String)
}