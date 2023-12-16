package com.study.kstream.stream

import com.study.kstream.model.Customer
import com.study.kstream.model.Order
import com.study.kstream.model.OrderEnriched
import com.study.kstream.model.Payment
import com.study.kstream.model.Topics.ORDERS
import com.study.kstream.model.Topics.PAYMENTS
import com.study.kstream.model.email.EmailTuple
import com.study.kstream.service.Emailer
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.StreamJoined
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.function.Function

/**
 * 분기해서 처리해야 하나?
 */
@Service
class EmailStream(private val emailer: Emailer) {
    private val joinedOrder = StreamJoined.with(ORDERS.keySerde, ORDERS.valueSerde, PAYMENTS.valueSerde)

    @Bean
    fun email(): Function<KStream<String, Order>, Function<KStream<String, Payment>, Function<GlobalKTable<Long, Customer>, KStream<String, OrderEnriched>>>> {
        return Function { orders: KStream<String, Order> ->
                Function { payments: KStream<String, Payment> ->
                    Function { customers: GlobalKTable<Long, Customer> ->
                    val paymentsByOrderId = payments.selectKey { _, payment -> payment.orderId }

                    orders.join(
                        paymentsByOrderId, ::EmailTuple, JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(1)), joinedOrder
                    ).join(
                            customers, { _, tuple -> tuple.order.customerId }, EmailTuple::setCustomer
                        ).peek { _, emailTuple -> emailer.sendEmail(emailTuple) }

                    orders.join(customers,
                        { _, order -> order.customerId },
                        { order, customer -> OrderEnriched(order.id, order.customerId, customer.level) })
                }
            }
        }
    }
}