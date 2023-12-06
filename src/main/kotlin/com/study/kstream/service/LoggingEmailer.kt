package com.study.kstream.service

import com.study.kstream.model.email.EmailTuple
import org.springframework.stereotype.Service

@Service
class LoggingEmailer: Emailer {
    override fun sendEmail(details: EmailTuple) {
        TODO("Not yet implemented")
    }
}