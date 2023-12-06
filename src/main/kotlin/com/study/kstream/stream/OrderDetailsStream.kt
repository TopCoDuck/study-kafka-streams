package com.study.kstream.stream

import com.study.kstream.model.*
import com.study.kstream.model.OrderValidationType.ORDER_DETAILS_CHECK
import com.study.kstream.model.OrderValidationResult.PASS
import com.study.kstream.model.OrderValidationResult.FAIL

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.util.function.Function

//@Configuration
//@Component
class OrderDetailsStream {

    //@Bean
    fun process(): Function<KStream<String, Order>, KStream<String, OrderValidation>> =
         Function {
            it.filter { _, value -> value.state == OrderState.CREATED}
                .map{ key, value -> KeyValue(key, OrderValidation(value.id, ORDER_DETAILS_CHECK, if(isValid(value)) PASS else FAIL)) }
         }



    private fun isValid(order: Order): Boolean {
        if (order.quantity < 0)
            return false
        if (order.price < 0)
            return false
        return true
    }
}