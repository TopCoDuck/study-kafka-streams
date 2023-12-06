package com.study.kstream.stream

import com.study.kstream.model.*
import com.study.kstream.model.OrderValidationType.ORDER_DETAILS_CHECK
import com.study.kstream.model.OrderValidationResult.PASS
import com.study.kstream.model.OrderValidationResult.FAIL

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.springframework.stereotype.Component
import java.util.function.Function
//Function activation issues while mapping the function 예상대로 동작하지 않음
//@Component("process")
class OrderDetailsStream2 : Function<KStream<String, Order>, KStream<String, OrderValidation>>{

    override fun apply(it: KStream<String, Order>): KStream<String, OrderValidation> {
        return it.peek {key, value -> print("테스트 하기(TEST) : $key $value") }
            //.filter { _, value -> value.state == OrderState.CREATED}
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