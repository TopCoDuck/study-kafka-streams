package com.study.kstream.model

import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class ProductTypeSerde : Serde<Product> {
    override fun configure(configs: Map<String?, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
    }

    override fun close() {
        super.close()
    }

    override fun serializer() =
        Serializer<Product> { topic, product -> product.toString().toByteArray() }


    override fun deserializer() =
        Deserializer<Product> { topic, bytes -> Product.valueOf(String(bytes))}
}
