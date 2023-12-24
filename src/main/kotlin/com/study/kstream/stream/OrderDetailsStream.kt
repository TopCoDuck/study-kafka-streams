package com.study.kstream.stream

import com.study.kstream.model.Order
import com.study.kstream.model.OrderState
import com.study.kstream.model.OrderValidation
import com.study.kstream.model.OrderValidationResult.FAIL
import com.study.kstream.model.OrderValidationResult.PASS
import com.study.kstream.model.OrderValidationType.ORDER_DETAILS_CHECK
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class OrderDetailsStream {

    @Bean
    fun process(): Function<KStream<String, Order>, KStream<String, OrderValidation>> {
        return Function {
            it.filter { _, value -> value.state == OrderState.CREATED }
                .map(::mappingResult)
        }
    }

    private fun mappingResult(key: String, value: Order) =
        KeyValue(
            key,
            OrderValidation(
                value.id,
                ORDER_DETAILS_CHECK,
                if (isValid(value)) PASS else FAIL
            )
        )

    private fun isValid(order: Order): Boolean {
        if (order.quantity < 0)
            return false
        if (order.price < 0)
            return false
        return true
    }
}