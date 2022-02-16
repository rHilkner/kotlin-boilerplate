package com.example.apiboilerplate.services.email

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.mail.MessagingException

@Service
class EmailService(private val mailSender: JavaMailSender) : EmailSender {

    companion object { private val log by ApiLogger() }

    @Async
    override fun send(to: String, text: String) {

        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, "utf-8")

            helper.setText(text, true)
            helper.setTo(to)
            helper.setSubject("Email teste")
            helper.setFrom("hello@boilerplate.com")

            mailSender.send(mimeMessage)

        } catch (e: MessagingException) {
            log.error("Failed to send email", e)
            throw ApiExceptionModule.Email.FailedToSendEmail(to)
        }
    }

}