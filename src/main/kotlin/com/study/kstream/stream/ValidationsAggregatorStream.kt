package com.study.kstream.stream


import com.study.kstream.model.Order
import com.study.kstream.model.OrderState
import com.study.kstream.model.OrderValidation
import com.study.kstream.model.OrderValidationResult.PASS
import com.study.kstream.model.Topics.ORDERS
import com.study.kstream.model.Topics.ORDER_VALIDATIONS
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.function.BiFunction

@Service
class ValidationsAggregatorStream {

    companion object {
        const val NUMBER_OR_RULES = 3
    }

    private val validationGrouped = Grouped
        .with(ORDER_VALIDATIONS.keySerde, ORDER_VALIDATIONS.valueSerde)

    private val joinOrders = StreamJoined
        .with(ORDERS.keySerde, Serdes.Long(), ORDERS.valueSerde)

    private val joinFailOrders = StreamJoined
        .with(ORDERS.keySerde, ORDER_VALIDATIONS.valueSerde, ORDERS.valueSerde)

    private val orderGrouped: Grouped<String, Order> = Grouped
        .with(ORDERS.keySerde, ORDERS.valueSerde)

    @Bean
    fun validationsAggregator(): BiFunction<KStream<String, OrderValidation>, KStream<String, Order>, KStream<String, Order>> {
        return BiFunction<KStream<String, OrderValidation>, KStream<String, Order>, KStream<String, Order>> { validations, orders ->
            val aggregate = validations.groupByKey(validationGrouped)
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(Duration.ofMinutes(5)))
                .aggregate(
                    { 0L },
                    { _, result, total -> if (PASS == result.validationResult) total + 1 else total },
                    { _, a, b -> b ?: a },
                    Materialized.with(null, Serdes.Long())
                )

            val validatedOrder = aggregate
                .toStream { windowedKey, _ -> windowedKey.key() }
                .filter { _, v -> v != null }
                .filter { _, total -> total >= NUMBER_OR_RULES }
                .join(
                    orders.filter { _, order -> OrderState.CREATED == order.state },
                    { _, order -> order.copy(state = OrderState.VALIDATED) },
                    JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)),
                    joinOrders
                )
//            TODO: Invalid topology: stream-thread 오류
//            val failOrder = validations.filter {_, rule -> FAIL == rule.validationResult}
//                .join(orders,
//                    {_ , order-> order.copy(state = OrderState.FAILED) } ,
//                    JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)),
//                     joinFailOrders)
//                .groupByKey(orderGrouped)
//                .reduce {order, _ -> order}
//                .toStream()
//
//            arrayOf(validatedOrder, failOrder)
            validatedOrder
        }
    }
}