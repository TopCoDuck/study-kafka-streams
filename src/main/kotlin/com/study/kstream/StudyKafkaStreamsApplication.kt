package com.study.kstream
import java.util.function.Function
import com.study.kstream.model.*
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class StudyKafkaStreamsApplication
{

    @Bean
    fun process(): Function<KStream<String, Order>, KStream<String, OrderValidation>> {
        return Function {
            it.filter { _, value -> value.state == OrderState.CREATED}
                .map{ key, value -> KeyValue(key, OrderValidation(value.id, OrderValidationType.ORDER_DETAILS_CHECK, if(isValid(value)) OrderValidationResult.PASS else OrderValidationResult.FAIL)) }
        }
    }

    private fun isValid(order: Order): Boolean {
        if (order.quantity < 0)
            return false
        if (order.price < 0)
            return false
        return true
    }
}


fun main(args: Array<String>) {
    runApplication<StudyKafkaStreamsApplication>(*args)
}
