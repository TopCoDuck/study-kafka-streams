package com.study.kstream.stream

import com.study.kstream.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.Stores
import org.springframework.context.annotation.Bean
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component
import java.util.stream.Stream

//@Component
class InventoryStream {
    /*
   companion object {
       private const val RESERVED_STOCK_STORE_NAME  = "store-of-reserved-stock"
  }
   @Bean
   fun inventory(): (KStream<String, Order>, KTable<Product, Int>, KTable<Product, Long>) -> KStream<Product?, KeyValue<Order, Int>> =
   { orders, warehouseInventory, reservedStock ->
       orders
           .selectKey {id, order -> order.product }
           .filter {id, order -> OrderState.CREATED == order.state}
           .join(warehouseInventory,
               ::KeyValue,
               Joined.with(ProductTypeSerde(), JsonSerde<Order>(), Serdes.Integer())
               )
           .transform { }

   }

   @Bean
   fun reservedStock() =
       Stores
           .keyValueStoreBuilder(Stores.persistentKeyValueStore(RESERVED_STOCK_STORE_NAME),
               ProductTypeSerde(), Serdes.Long()
               )*/
           // .withLoggingEnabled(::HashMap<>())

}
