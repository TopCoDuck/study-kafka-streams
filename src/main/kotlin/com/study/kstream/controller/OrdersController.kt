package com.study.kstream.controller

import com.study.kstream.model.OrderBean
import com.study.kstream.model.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture


@RestController
class OrdersController(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    @PostMapping("/orders")
    fun submitOrder(order: OrderBean): String {
        val bean = OrderBean.formBean(order)
        kafkaTemplate.send(Topics.ORDERS.name, bean.id, bean).get()
        return bean.id
    }
}
