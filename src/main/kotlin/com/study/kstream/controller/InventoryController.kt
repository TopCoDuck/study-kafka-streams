package com.study.kstream.controller

import com.study.kstream.model.Product
import com.study.kstream.model.Topics
import org.apache.kafka.streams.KeyValue
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(private val kafkaTemplate: KafkaTemplate<String, Int>) {

    @PostMapping(value = ["/inventory"])
    fun addInventory(): String {
        val quantityUnderpants = 2000
        val quantityJumpers = 2000

        val inventorys = listOf(
            KeyValue(Product.UNDERPANTS, quantityUnderpants),
            KeyValue(Product.JUMPERS, quantityJumpers)
        )
        inventorys.forEach { inventory ->
            kafkaTemplate.send(Topics.WAREHOUSE_INVENTORY.name, inventory.key.toString(), inventory.value)
        }

        return "인밴토리에 상품이 등록되었습니다."
    }

}