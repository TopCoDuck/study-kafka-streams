package com.study.kstream.service

import com.study.kstream.model.email.EmailTuple

interface Emailer {
    fun sendEmail(details: EmailTuple)
}