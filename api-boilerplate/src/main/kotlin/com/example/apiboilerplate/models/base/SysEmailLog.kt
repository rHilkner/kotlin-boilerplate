package com.example.apiboilerplate.models.base

import com.example.apiboilerplate.enums.EmailSentStatusCd
import javax.persistence.*

@Entity(name = "SysEmailLog")
@Table(name = "sys_email_log", schema = "public")
class SysEmailLog : DbAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_log_id")
    var errorLogId: Long? = null

    @Column(name = "call_log_id")
    var callLogId: Long? = null

    @Column(name = "from_addr")
    var fromAddr: String? = null

    @Column(name = "to_addr")
    var toAddr: String? = null

    @Column(name = "subject")
    lateinit var subject: String

    @Column(name = "body")
    lateinit var body: String

    @Convert(converter = EmailSentStatusCd.Converter::class)
    @Column(name = "sent_status_cd")
    var sentStatusCd: EmailSentStatusCd? = null

    constructor()
    constructor(callLogId: Long?, fromAddr: String, toAddr: String, subject: String, body: String, sentStatusCd: EmailSentStatusCd?) {
        this.callLogId = callLogId
        this.fromAddr = fromAddr
        this.toAddr = toAddr
        this.subject = subject
        this.body = body
        this.sentStatusCd = sentStatusCd
    }

}