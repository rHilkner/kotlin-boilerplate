package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.AppEmails
import com.example.apiboilerplate.enums.EmailSentStatusCd
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.base.SysEmailLog
import com.example.apiboilerplate.repositories.SysEmailLogRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val emailLogRepository: SysEmailLogRepository
) {

    companion object { private val log by ApiLogger() }

    @Async
    fun send(from: AppEmails, to: String, subject: String, html: String) {

        log.info("Sending email from [$from] to [$to] with subject [$subject]")

        val mimeMessage = mimeMessage(to, from.address, subject, html)

        // Save attempt to send email to EmailLog
        val currentCallLogId = ApiSessionContext.getCurrentApiCallContext().sysCallLog.callLogId
        var emailLog = SysEmailLog(currentCallLogId, from.address, to, subject, html, null)
        emailLog =  emailLogRepository.save(emailLog)

        try {
            mailSender.send(mimeMessage)
            // Save to database in case of success
            emailLog.sentStatusCd = EmailSentStatusCd.OK
            emailLogRepository.save(emailLog)
        } catch (e: MessagingException) {
            log.error("Failed to send email", e)
            // Save to database in case of failure
            emailLog.sentStatusCd = EmailSentStatusCd.FAIL
            emailLogRepository.save(emailLog)
            // Throw exception
            throw ApiExceptionModule.Email.FailedToSendEmail(to)
        }

        log.info("Email to [$to] sent successfully")

    }

    private fun mimeMessage(to: String, from: String, subject: String, html: String): MimeMessage {
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, "utf-8")

        helper.setTo(to)
        helper.setFrom(from)
        helper.setSubject(subject)
        helper.setText(html, true)
        return mimeMessage
    }

}