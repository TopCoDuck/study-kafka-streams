package com.study.kstream.model

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.springframework.kafka.support.serializer.JsonSerde
object Schemas {
    val ORDER_VALUE_SERDE = JsonSerde(OrderValue::class.java)
}
object Topics {

    val ORDERS = Topic("orders", Serdes.String(), JsonSerde(Order::class.java))
    val ORDERS_ENRICHED = Topic("orders-enriched", Serdes.String(), JsonSerde(OrderEnriched::class.java))
    val PAYMENTS = Topic("payments", Serdes.String(), JsonSerde(Payment::class.java))
    val CUSTOMERS = Topic("customers", Serdes.Long(), JsonSerde(Customer::class.java))
    val ORDER_VALIDATIONS = Topic("order-validations", Serdes.String(), JsonSerde(OrderValidation::class.java))
    val WAREHOUSE_INVENTORY = Topic("warehouse-inventory", ProductTypeSerde(), Serdes.Integer())

}
class Topic<K, V> (val name: String,
                   val keySerde: Serde<K>,
                   val valueSerde: Serde<V>,
)