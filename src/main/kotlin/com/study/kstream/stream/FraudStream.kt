package com.study.kstream.stream

import com.study.kstream.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.springframework.context.annotation.Bean
import org.springframework.kafka.support.serializer.JsonSerde
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

            val aggregate = orders.groupBy({ _, order -> order.customerId }, Grouped.with(Serdes.Long(), JsonSerde(Order::class.java)))
                .windowedBy(SessionWindows.with(Duration.ofHours(1)))
                .aggregate(OrderValue::empty,
                    {_, order, total ->  OrderValue(order, total.value + order.quantity * order.price) },
                    {_, a, b -> simpleMerge(a, b)},
                    Materialized.with(Serdes.Long(), JsonSerde(OrderValue::class.java))
                )

            val ordersWithTotals = aggregate.toStream {windowedKey, _ -> windowedKey.key()}
                .filter {_, v -> v != null}
                .selectKey {_, orderValue -> orderValue.order!!.id }

            ordersWithTotals.mapValues { orderValue ->
                    OrderValidation(orderValue.order!!.id,
                        OrderValidationType.FRAUD_CHECK,
                        if(orderValue.value >=  FRAUD_LIMIT) OrderValidationResult.FAIL else OrderValidationResult.PASS)}
        }

    private fun simpleMerge(a: OrderValue?, b:OrderValue) = OrderValue(b.order, (a?.value ?: 0.0) + b.value)

    /**
     *     final KStream<String, OrderValue>[] forks = ordersWithTotals.branch(
     *         (id, orderValue) -> orderValue.getValue() >= FRAUD_LIMIT,
     *         (id, orderValue) -> orderValue.getValue() < FRAUD_LIMIT);
     *
     *     forks[0].mapValues(
     *         orderValue -> new OrderValidation(orderValue.getOrder().getId(), FRAUD_CHECK, FAIL))
     *         .to(ORDER_VALIDATIONS.name(), Produced
     *             .with(ORDER_VALIDATIONS.keySerde(), ORDER_VALIDATIONS.valueSerde()));
     *
     *     forks[1].mapValues(
     *         orderValue -> new OrderValidation(orderValue.getOrder().getId(), FRAUD_CHECK, PASS))
     *         .to(ORDER_VALIDATIONS.name(), Produced
     *             .with(ORDER_VALIDATIONS.keySerde(), ORDER_VALIDATIONS.valueSerde()));
     */
}