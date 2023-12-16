package com.study.kstream.stream

import com.study.kstream.model.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.StateStore
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.Stores
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.util.function.BiFunction


@Service
class InventoryStream {
    companion object {
        private const val RESERVED_STOCK_STORE_NAME = "store-of-reserved-stock"
    }

    @Bean
    fun inventory(): BiFunction<KStream<String, Order>, KTable<Product, Int>, KStream<String, OrderValidation>> {
        return BiFunction<KStream<String, Order>, KTable<Product, Int>, KStream<String, OrderValidation>> { orders, warehouseInventory ->
            orders
                .selectKey { _, order -> order.product }
                .filter { _, order -> OrderState.CREATED == order.state }
                .join(
                    warehouseInventory,
                    ::KeyValue,
                    Joined.with(Topics.WAREHOUSE_INVENTORY.keySerde,
                        Topics.ORDERS.valueSerde,
                        Serdes.Integer())
                )
                .transform(::InventoryValidator, RESERVED_STOCK_STORE_NAME)
        }
    }
    /*    @Bean
        fun inventory(): Function<KStream<String, Order>, Function<KTable<Product, Int>, Function<KTable<Product, Long>, KStream<String, OrderValidation>>>> {
            return Function<KStream<String, Order>, Function<KTable<Product, Int>, Function<KTable<Product, Long>, KStream<String, OrderValidation>>>> { orders: KStream<String, Order> ->
                    Function<KTable<Product, Int>, Function<KTable<Product, Long>, KStream<String, OrderValidation>>> { warehouseInventory: KTable<Product, Int> ->
                        Function<KTable<Product, Long>, KStream<String, OrderValidation>> { reservedStock: KTable<Product, Long> ->
                        orders
                            .selectKey { id, order -> order.product }
                            .filter { id, order -> OrderState.CREATED == order.state }
                            .join(
                                warehouseInventory,
                                ::KeyValue,
                                Joined.with(ProductTypeSerde(), JsonSerde<Order>(), Serdes.Integer())
                            )
                            .transform(::InventoryValidator, RESERVED_STOCK_STORE_NAME)
                    }
                }
            }
        }*/


    class InventoryValidator : Transformer<Product, KeyValue<Order, Int>, KeyValue<String, OrderValidation>> {
        private lateinit var reservedStocksStore: KeyValueStore<Product, Long>
        override fun init(context: ProcessorContext) {
            reservedStocksStore =
                context.getStateStore<StateStore>(RESERVED_STOCK_STORE_NAME) as KeyValueStore<Product, Long>
        }

        override fun transform(
            productId: Product,
            orderAndStock: KeyValue<Order, Int>
        ): KeyValue<String, OrderValidation> {

            val validated: OrderValidation
            val order: Order = orderAndStock.key
            val warehouseStockCount: Int = orderAndStock.value

            val reserved = reservedStocksStore[order.product] ?: 0L

            if (warehouseStockCount - reserved - order.quantity >= 0) {

                reservedStocksStore.put(order.product, reserved + order.quantity)
                validated = OrderValidation(
                    order.id,
                    OrderValidationType.INVENTORY_CHECK,
                    OrderValidationResult.PASS
                )
            } else {
                validated = OrderValidation(
                    order.id,
                    OrderValidationType.INVENTORY_CHECK,
                    OrderValidationResult.FAIL
                )
            }
            return KeyValue.pair(validated.orderId, validated)
        }

        override fun close() {
        }

    }

    @Bean
    fun reservedStock() =
        Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore(RESERVED_STOCK_STORE_NAME),
                ProductTypeSerde(), Serdes.Long()
            )
            .withLoggingEnabled(HashMap())

}
