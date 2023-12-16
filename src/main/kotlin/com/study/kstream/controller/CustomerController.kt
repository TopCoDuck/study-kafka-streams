package com.study.kstream.controller

import com.study.kstream.model.Customer
import com.study.kstream.model.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    @PostMapping("/customer")
    fun addCustomer(customer: Customer) {
        kafkaTemplate.send(Topics.CUSTOMERS.name, customer.id.toString(), customer)
    }
}
