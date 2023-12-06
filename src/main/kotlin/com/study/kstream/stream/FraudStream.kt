package com.study.kstream.stream

import com.study.kstream.model.*
import org.apache.kafka.streams.kstream.*
import org.springframework.context.annotation.Bean
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component
import java.time.Duration

//@Component
class FraudStream {

    //@Bean
    fun fraud(): (KStream<String, Order>) -> KStream<String?, OrderValidation> =
        {
            val orders = it.filter { _, order -> OrderState.CREATED == order.state }

            val aggregate = orders.groupBy { _, order -> order.customerId }
                .windowedBy(SessionWindows.with(Duration.ofHours(1)))
                .aggregate(OrderValue::empty,
                    {_, order, total ->  OrderValue(order, total.value + order.quantity * order.price) },
                    {_, a, b -> simpleMerge(a, b)},
                    Materialized.with(null, JsonSerde<OrderValue>()))

            val ordersWithTotals = aggregate.toStream {windowedKey, _ -> windowedKey.key()}
                .filter {_, v -> v != null}
                .selectKey {_, orderValue -> orderValue.order!!.id }

            ordersWithTotals.mapValues { orderValue ->
                    OrderValidation(orderValue.order!!.id,
                        OrderValidationType.FRAUD_CHECK,
                        if(orderValue.value >=  200) OrderValidationResult.FAIL else OrderValidationResult.PASS)}
        }

    private fun simpleMerge(a: OrderValue?, b:OrderValue) = OrderValue(b.order, (a?.value ?: 0.0) + b.value)
}