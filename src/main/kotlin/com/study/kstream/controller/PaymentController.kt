package com.study.kstream.controller

import com.study.kstream.model.Payment
import com.study.kstream.model.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    @PostMapping("/payment")
    fun addPayment(payment: Payment) {
        kafkaTemplate.send(Topics.PAYMENTS.name, payment.id, payment)
    }
}