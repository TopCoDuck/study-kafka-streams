package com.study.kstream.stream

import com.study.kstream.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.function.Function

@Service
class FraudStream {
    companion object {
        private const val FRAUD_LIMIT  = 2000
    }
    @Bean
    fun fraud(): Function<KStream<String, Order>, KStream<String?, OrderValidation>>  =
        Function {
            val orders = it.filter { _, order -> OrderState.CREATED == order.state }

            val aggregate = orders.groupBy({ _, order -> order.customerId }, Grouped.with(Serdes.Long(), Topics.ORDERS.valueSerde))
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(Duration.ofHours(1)))
                .aggregate(OrderValue::empty,
                    {_, order, total ->  OrderValue(order, total.value + order.quantity * order.price) },
                    {_, ov1, ov2 -> simpleMerge(ov1, ov2)},
                    Materialized.with(Serdes.Long(), Schemas.ORDER_VALUE_SERDE)
                )

            val ordersWithTotals = aggregate.toStream {windowedKey, _ -> windowedKey.key()}
                .filter {_, v -> v != null}
                .selectKey {_, orderValue -> orderValue.order!!.id }

            ordersWithTotals.mapValues { orderValue ->
                    OrderValidation(orderValue.order!!.id,
                        OrderValidationType.FRAUD_CHECK,
                        if(orderValue.value >=  FRAUD_LIMIT) OrderValidationResult.FAIL else OrderValidationResult.PASS)}
        }

    private fun simpleMerge(ov1: OrderValue?, ov2:OrderValue) =
        OrderValue(ov2.order, (ov1?.value ?: 0.0) + ov2.value)
}