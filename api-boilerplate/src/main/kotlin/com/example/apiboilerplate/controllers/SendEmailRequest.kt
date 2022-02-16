package com.example.apiboilerplate.controllers

data class SendEmailRequest (
    val to: String,
    val text: String
)