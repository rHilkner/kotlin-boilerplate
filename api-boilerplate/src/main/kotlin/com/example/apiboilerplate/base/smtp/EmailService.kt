package com.example.apiboilerplate.base.smtp

import com.example.apiboilerplate.base.logger.ApiLogger
import lombok.extern.java.Log
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage

@Log
@Slf4j
@Service
class EmailService {

    companion object { private val log by ApiLogger() }

    @Value("\${smtp.server}")
    private val EMAIL_SERVER: String? = null

    fun sendHtmlStaples(text: String, title: String, emailFrom: String, recipients: List<String>): Boolean {
        return try {
            sendMail(EMAIL_SERVER!!, emailFrom, recipients, title, text)
            true
        } catch (e: Exception) {
            log.error(
                "Error sending email with title [{}] to recipients [{}]",
                title,
                recipients.toString(),
                e
            )
            false
        }
    }

    /**
     * Send email to multiple recipients
     *
     * @param mailServer  email server url
     * @param from        email from address
     * @param recipients  string array with all recipients
     * @param subject     email subject
     * @param messageBody email body
     * @throws MessagingException
     * @throws AddressException
     */
    @Throws(MessagingException::class, AddressException::class)
    private fun sendMail(
        mailServer: String,
        from: String,
        recipients: List<String>,
        subject: String,
        messageBody: String
    ) {
        val message: MimeMessage = getMessage(mailServer, from, subject, messageBody)
        message.setText(messageBody)
        for (to in recipients) {
            message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
        }
        Transport.send(message)
    }

    /**
     * Build email message
     *
     * @param mailServer  email server url
     * @param from        email from address
     * @param subject     email subject
     * @param messageBody message body
     * @return
     * @throws MessagingException
     */
    @Throws(MessagingException::class)
    private fun getMessage(mailServer: String, from: String, subject: String, messageBody: String): MimeMessage {
        val props = System.getProperties()
        props["mail.smtp.host"] = mailServer
        val session: Session = Session.getDefaultInstance(props, null)
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(from))
        message.setSubject(subject, "UTF-8")
        val messageBodyPart = MimeBodyPart()
        messageBodyPart.setContent(messageBody, "text/html; charset=utf-8")
        return message
    }

}